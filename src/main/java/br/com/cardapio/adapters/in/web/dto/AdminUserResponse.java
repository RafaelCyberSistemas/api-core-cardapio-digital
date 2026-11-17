package br.com.cardapio.adapters.in.web.dto;

import br.com.cardapio.domain.model.AdminUser;
import br.com.cardapio.domain.model.UserRole;

import java.util.Set;
import java.util.UUID;

public record AdminUserResponse(
        UUID id,
        String name,
        String email,
        UserRole role,
        UUID estabelecimentoId,
        boolean active,
        Set<String> permissions
) {
    public static AdminUserResponse from(AdminUser user) {
        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getEstabelecimentoId(),
                user.isActive(),
                user.getPermissions()
        );
    }
}
