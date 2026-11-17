package br.com.cardapio.adapters.in.web;

import br.com.cardapio.adapters.in.web.dto.AddPermissionRequest;
import br.com.cardapio.adapters.in.web.dto.AdminUserResponse;
import br.com.cardapio.adapters.in.web.dto.CreateAdminUserRequest;
import br.com.cardapio.adapters.in.web.dto.ProvisionarNovoEstabelecimentoRequest;
import br.com.cardapio.adapters.in.web.dto.ReplacePermissionsRequest;
import br.com.cardapio.adapters.in.web.dto.UpdateMenuAdminRequest;
import br.com.cardapio.application.service.PlatformAdminUserService;
import br.com.cardapio.domain.model.Endereco;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/platform")
@PreAuthorize("hasRole('ADMIN_PLATAFORMA')")
public class PlatformAdminUserController {
    private final PlatformAdminUserService service;

    public PlatformAdminUserController(PlatformAdminUserService service) {
        this.service = service;
    }

    @PostMapping("/admin-users/provisionar-novo-estabelecimento")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminUserResponse provisionarNovoEstabelecimento(@RequestBody @Valid ProvisionarNovoEstabelecimentoRequest request) {
        Endereco endereco = new Endereco(
                request.endereco().logradouro(),
                request.endereco().bairro(),
                request.endereco().cidade(),
                request.endereco().codigoPostal()
        );
        return AdminUserResponse.from(service.provisionarComNovoEstabelecimento(
                request.nomeRazaoSocial(),
                request.cpfCnpj(),
                request.nomeResponsavel(),
                request.telefone(),
                request.whatsapp(),
                endereco,
                request.publicSlug(),
                request.admin().name(),
                request.admin().email(),
                request.admin().password()
        ));
    }

    @PostMapping("/admin-users")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminUserResponse createAdmin(@RequestBody @Valid CreateAdminUserRequest request) {
        return AdminUserResponse.from(service.createAdminUser(
                request.name(),
                request.email(),
                request.password(),
                request.role(),
                request.estabelecimentoId()
        ));
    }

    @GetMapping("/admin-users")
    public List<AdminUserResponse> listMenuAdmins() {
        return service.listMenuAdmins().stream().map(AdminUserResponse::from).toList();
    }

    @PutMapping("/admin-users/{id}")
    public AdminUserResponse updateMenuAdmin(@PathVariable UUID id, @RequestBody @Valid UpdateMenuAdminRequest request) {
        return AdminUserResponse.from(service.updateMenuAdmin(id, request.name(), request.active()));
    }

    @DeleteMapping("/admin-users/{id}")
    public ResponseEntity<Void> deleteMenuAdmin(@PathVariable UUID id) {
        service.deleteMenuAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin-users/{id}/permissions")
    public AdminUserResponse replacePermissions(@PathVariable UUID id, @RequestBody @Valid ReplacePermissionsRequest request) {
        return AdminUserResponse.from(service.replacePermissions(id, sanitize(request.permissions())));
    }

    @PostMapping("/admin-users/{id}/permissions")
    public AdminUserResponse addPermission(@PathVariable UUID id, @RequestBody @Valid AddPermissionRequest request) {
        return AdminUserResponse.from(service.addPermission(id, request.permission().trim().toUpperCase()));
    }

    @DeleteMapping("/admin-users/{id}/permissions/{permission}")
    public AdminUserResponse removePermission(@PathVariable UUID id, @PathVariable String permission) {
        return AdminUserResponse.from(service.removePermission(id, permission.trim().toUpperCase()));
    }

    @GetMapping("/estabelecimentos/{estabelecimentoId}/admin-users")
    public List<AdminUserResponse> listarPorEstabelecimento(@PathVariable UUID estabelecimentoId, @RequestParam(defaultValue = "true") boolean onlyMenuAdmins) {
        return service.listarPorEstabelecimento(estabelecimentoId).stream()
                .filter(user -> !onlyMenuAdmins || user.getRole().name().equals("ADMIN_CARDAPIO"))
                .map(AdminUserResponse::from)
                .toList();
    }

    private Set<String> sanitize(Set<String> permissions) {
        return permissions.stream().map(value -> value.trim().toUpperCase()).collect(java.util.stream.Collectors.toSet());
    }
}
