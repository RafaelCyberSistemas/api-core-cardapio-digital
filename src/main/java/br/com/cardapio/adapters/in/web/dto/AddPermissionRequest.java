package br.com.cardapio.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AddPermissionRequest(
        @NotBlank String permission
) {
}
