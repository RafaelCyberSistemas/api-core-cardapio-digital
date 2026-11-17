package br.com.cardapio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminRequirementsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void rf01_shouldAuthenticateAdministrativeUser() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"platform@cardapio.local","password":"Admin@123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.role").value("ADMIN_PLATAFORMA"));
    }

    @Test
    void rf02_shouldContainAllProfiles() {
        assertThat(br.com.cardapio.domain.model.UserRole.values())
                .extracting(Enum::name)
                .contains("ADMIN_PLATAFORMA", "ADMIN_CARDAPIO", "VISITANTE_CLIENTE");
    }

    @Test
    void rf03_shouldCreateEstablishmentMenuAndAdminAtomically() throws Exception {
        String token = loginPlatform();
        JsonNode created = provisionarNovoEstabelecimento(token, "resto-rf03");
        String estabelecimentoId = created.get("estabelecimentoId").asText();

        mockMvc.perform(get("/api/v1/platform/estabelecimentos/{id}/admin-users", estabelecimentoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estabelecimentoId").value(estabelecimentoId));
    }

    @Test
    void rf04_shouldListUpdateAndDeleteMenuAdmins() throws Exception {
        String token = loginPlatform();
        JsonNode created = provisionarNovoEstabelecimento(token, "resto-rf04");
        String userId = created.get("id").asText();

        mockMvc.perform(put("/api/v1/platform/admin-users/{id}", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Nome Atualizado","active":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Atualizado"));

        mockMvc.perform(delete("/api/v1/platform/admin-users/{id}", userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void rf05_shouldManagePermissions() throws Exception {
        String token = loginPlatform();
        String email = "platform+" + UUID.randomUUID() + "@mail.com";
        String createBody = """
                {"name":"Gestor","email":"%s","password":"Senha@123","role":"ADMIN_PLATAFORMA"}
                """.formatted(email);
        String createdPayload = mockMvc.perform(post("/api/v1/platform/admin-users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String userId = objectMapper.readTree(createdPayload).get("id").asText();

        mockMvc.perform(put("/api/v1/platform/admin-users/{id}/permissions", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"permissions":["USER_MANAGEMENT"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions[0]").value("USER_MANAGEMENT"));

        mockMvc.perform(post("/api/v1/platform/admin-users/{id}/permissions", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"permission":"AUDIT_VIEW"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions").isArray());

        mockMvc.perform(delete("/api/v1/platform/admin-users/{id}/permissions/{permission}", userId, "AUDIT_VIEW")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void rf06_shouldCreateMenuAdminForExistingEstablishment() throws Exception {
        String token = loginPlatform();
        JsonNode created = provisionarNovoEstabelecimento(token, "resto-rf06");
        String estabelecimentoId = created.get("estabelecimentoId").asText();
        String email = "menu+" + UUID.randomUUID() + "@mail.com";

        mockMvc.perform(post("/api/v1/platform/admin-users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Novo Admin","email":"%s","password":"Senha@123","role":"ADMIN_CARDAPIO","estabelecimentoId":"%s"}
                                """.formatted(email, estabelecimentoId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estabelecimentoId").value(estabelecimentoId));
    }

    @Test
    void rf07_shouldValidateExistingEstablishmentAndMenuBeforeLinking() throws Exception {
        String token = loginPlatform();
        mockMvc.perform(post("/api/v1/platform/admin-users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Falha","email":"%s","password":"Senha@123","role":"ADMIN_CARDAPIO","estabelecimentoId":"%s"}
                                """.formatted("bad+" + UUID.randomUUID() + "@mail.com", UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }

    @Test
    void rf08_shouldCreatePlatformAdminWithoutEstablishment() throws Exception {
        String token = loginPlatform();
        mockMvc.perform(post("/api/v1/platform/admin-users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Admin P","email":"%s","password":"Senha@123","role":"ADMIN_PLATAFORMA"}
                                """.formatted("platform2+" + UUID.randomUUID() + "@mail.com")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estabelecimentoId").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void rf09_shouldAllowMultipleMenuAdminsPerEstablishment() throws Exception {
        String token = loginPlatform();
        JsonNode created = provisionarNovoEstabelecimento(token, "resto-rf09");
        String estabelecimentoId = created.get("estabelecimentoId").asText();

        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/api/v1/platform/admin-users")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Admin %d","email":"%s","password":"Senha@123","role":"ADMIN_CARDAPIO","estabelecimentoId":"%s"}
                                    """.formatted(i, "rf09+" + UUID.randomUUID() + "@mail.com", estabelecimentoId)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/v1/platform/estabelecimentos/{id}/admin-users", estabelecimentoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
    }

    @Test
    void rf10_shouldRequireExactlyOneEstablishmentForMenuAdmin() throws Exception {
        String token = loginPlatform();
        mockMvc.perform(post("/api/v1/platform/admin-users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Sem Vinculo","email":"%s","password":"Senha@123","role":"ADMIN_CARDAPIO"}
                                """.formatted("rf10+" + UUID.randomUUID() + "@mail.com")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rf11_shouldQueryMenuAdminsByEstablishmentIndex() throws Exception {
        String token = loginPlatform();
        JsonNode createdA = provisionarNovoEstabelecimento(token, "resto-rf11-a");
        JsonNode createdB = provisionarNovoEstabelecimento(token, "resto-rf11-b");
        String estabelecimentoA = createdA.get("estabelecimentoId").asText();
        String estabelecimentoB = createdB.get("estabelecimentoId").asText();

        String responseA = mockMvc.perform(get("/api/v1/platform/estabelecimentos/{id}/admin-users", estabelecimentoA)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(responseA).contains(estabelecimentoA).doesNotContain(estabelecimentoB);
    }

    @Test
    void rf12_shouldBlockCrossEstablishmentAccess() throws Exception {
        String platformToken = loginPlatform();
        JsonNode estOne = provisionarNovoEstabelecimento(platformToken, "resto-rf12-a");
        JsonNode estTwo = provisionarNovoEstabelecimento(platformToken, "resto-rf12-b");

        String menuAdminToken = login(estOne.get("email").asText(), "Senha@123");
        String estOneId = estOne.get("estabelecimentoId").asText();
        String estTwoId = estTwo.get("estabelecimentoId").asText();

        mockMvc.perform(get("/api/v1/menu-admin/estabelecimentos/{id}/scope-check", estOneId)
                        .header("Authorization", "Bearer " + menuAdminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/menu-admin/estabelecimentos/{id}/scope-check", estTwoId)
                        .header("Authorization", "Bearer " + menuAdminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void rf13_shouldRequireMandatoryFieldsForProvisioningEstablishment() throws Exception {
        String token = loginPlatform();
        String email = "invalido+" + UUID.randomUUID() + "@mail.com";
        mockMvc.perform(post("/api/v1/platform/admin-users/provisionar-novo-estabelecimento")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeRazaoSocial":"Restaurante Inválido",
                                  "cpfCnpj":"12345678901234",
                                  "nomeResponsavel":"Responsável",
                                  "telefone":"11999990000",
                                  "whatsapp":"11999990001",
                                  "publicSlug":"slug-invalido-%s",
                                  "admin":{"name":"Primeiro Admin","email":"%s","password":"Senha@123"}
                                }
                                """.formatted(UUID.randomUUID(), email)))
                .andExpect(status().isBadRequest());
    }

    private JsonNode provisionarNovoEstabelecimento(String token, String slugPrefix) throws Exception {
        String email = slugPrefix + "+" + UUID.randomUUID() + "@mail.com";
        String payload = mockMvc.perform(post("/api/v1/platform/admin-users/provisionar-novo-estabelecimento")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeRazaoSocial":"%s",
                                  "cpfCnpj":"%s",
                                  "nomeResponsavel":"%s",
                                  "telefone":"11999990000",
                                  "whatsapp":"11999990001",
                                  "publicSlug":"%s",
                                  "endereco":{
                                    "logradouro":"Rua Principal, 123",
                                    "bairro":"Centro",
                                    "cidade":"São Paulo",
                                    "codigoPostal":"01000-000"
                                  },
                                  "admin":{"name":"Primeiro Admin","email":"%s","password":"Senha@123"}
                                }
                                """.formatted(
                                "Restaurante " + slugPrefix,
                                "12345678000190",
                                "Responsável " + slugPrefix,
                                slugPrefix + "-" + UUID.randomUUID(),
                                email
                        )))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(payload);
    }

    private String loginPlatform() throws Exception {
        return login("platform@cardapio.local", "Admin@123");
    }

    private String login(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }
}
