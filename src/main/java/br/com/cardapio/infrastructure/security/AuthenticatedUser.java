package br.com.cardapio.infrastructure.security;

import br.com.cardapio.domain.model.UserRole;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        String email,
        UserRole role,
        UUID estabelecimentoId,
        Set<String> permissions
) {
}
