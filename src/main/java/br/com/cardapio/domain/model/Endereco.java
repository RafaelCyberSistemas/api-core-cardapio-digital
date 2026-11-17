package br.com.cardapio.domain.model;

import java.util.Objects;

public record Endereco(
        String logradouro,
        String bairro,
        String cidade,
        String codigoPostal
) {
    public Endereco {
        logradouro = validarCampo(logradouro, "logradouro");
        bairro = validarCampo(bairro, "bairro");
        cidade = validarCampo(cidade, "cidade");
        codigoPostal = validarCampo(codigoPostal, "codigoPostal");
    }

    private static String validarCampo(String valor, String nomeCampo) {
        Objects.requireNonNull(valor, nomeCampo);
        if (valor.isBlank()) {
            throw new IllegalArgumentException(nomeCampo + " é obrigatório");
        }
        return valor;
    }
}
