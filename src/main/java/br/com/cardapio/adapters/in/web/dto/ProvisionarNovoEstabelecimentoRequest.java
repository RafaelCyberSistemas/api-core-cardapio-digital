package br.com.cardapio.adapters.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProvisionarNovoEstabelecimentoRequest(
        @NotBlank String nomeRazaoSocial,
        @NotBlank String cpfCnpj,
        @NotBlank String nomeResponsavel,
        @NotBlank String telefone,
        @NotBlank String whatsapp,
        @NotBlank String publicSlug,
        @Valid @NotNull EnderecoPayload endereco,
        @Valid @NotNull AdminPayload admin
) {
    public record EnderecoPayload(
            @NotBlank String logradouro,
            @NotBlank String bairro,
            @NotBlank String cidade,
            @NotBlank String codigoPostal
    ) {
    }

    public record AdminPayload(
            @NotBlank String name,
            @Email @NotBlank String email,
            @NotBlank String password
    ) {
    }
}
