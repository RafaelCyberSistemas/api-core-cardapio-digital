package br.com.cardapio.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateMenuAdminRequest(
        @NotBlank String name,
        boolean active
) {
}
