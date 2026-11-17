package br.com.cardapio.adapters.in.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record ReplacePermissionsRequest(
        @NotNull Set<String> permissions
) {
}
