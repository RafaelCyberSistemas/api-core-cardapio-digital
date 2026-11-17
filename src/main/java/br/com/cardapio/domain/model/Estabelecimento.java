package br.com.cardapio.domain.model;

import java.util.Objects;
import java.util.UUID;

public record Estabelecimento(
        UUID id,
        String nomeRazaoSocial,
        String cpfCnpj,
        String nomeResponsavel,
        String telefone,
        String whatsapp,
        Endereco endereco,
        String publicSlug
) {
    public Estabelecimento {
        Objects.requireNonNull(id, "id");
        nomeRazaoSocial = validarCampo(nomeRazaoSocial, "nomeRazaoSocial");
        cpfCnpj = validarCampo(cpfCnpj, "cpfCnpj");
        nomeResponsavel = validarCampo(nomeResponsavel, "nomeResponsavel");
        telefone = validarCampo(telefone, "telefone");
        whatsapp = validarCampo(whatsapp, "whatsapp");
        Objects.requireNonNull(endereco, "endereco");
        publicSlug = validarCampo(publicSlug, "publicSlug");
    }

    private static String validarCampo(String valor, String nomeCampo) {
        Objects.requireNonNull(valor, nomeCampo);
        if (valor.isBlank()) {
            throw new IllegalArgumentException(nomeCampo + " é obrigatório");
        }
        return valor;
    }
}
