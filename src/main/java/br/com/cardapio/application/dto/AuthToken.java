package br.com.cardapio.application.dto;

import br.com.cardapio.domain.model.UserRole;

import java.util.Set;
import java.util.UUID;

public record AuthToken(
        String accessToken,
        UUID userId,
        String email,
        UserRole role,
        UUID estabelecimentoId,
        Set<String> permissions
) {
}
