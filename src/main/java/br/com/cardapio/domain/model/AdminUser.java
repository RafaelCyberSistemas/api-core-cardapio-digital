package br.com.cardapio.domain.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class AdminUser {
    private final UUID id;
    private String name;
    private final String email;
    private final String passwordHash;
    private UserRole role;
    private UUID estabelecimentoId;
    private boolean active;
    private final Set<String> permissions;

    public AdminUser(
            UUID id,
            String name,
            String email,
            String passwordHash,
            UserRole role,
            UUID estabelecimentoId,
            boolean active,
            Set<String> permissions
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email).toLowerCase();
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.role = Objects.requireNonNull(role);
        this.permissions = new HashSet<>(permissions == null ? Set.of() : permissions);
        this.active = active;
        vincularEstabelecimento(estabelecimentoId);
    }

    public void vincularEstabelecimento(UUID estabelecimentoId) {
        if (role == UserRole.ADMIN_CARDAPIO && estabelecimentoId == null) {
            throw new IllegalArgumentException("ADMIN_CARDAPIO deve possuir estabelecimento");
        }
        if (role != UserRole.ADMIN_CARDAPIO) {
            this.estabelecimentoId = null;
            return;
        }
        this.estabelecimentoId = estabelecimentoId;
    }

    public void changeRole(UserRole role, UUID estabelecimentoId) {
        this.role = Objects.requireNonNull(role);
        vincularEstabelecimento(estabelecimentoId);
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void replacePermissions(Set<String> values) {
        permissions.clear();
        permissions.addAll(values);
    }

    public void addPermission(String permission) {
        permissions.add(permission);
    }

    public void removePermission(String permission) {
        permissions.remove(permission);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public UUID getEstabelecimentoId() {
        return estabelecimentoId;
    }

    public boolean isActive() {
        return active;
    }

    public Set<String> getPermissions() {
        return Set.copyOf(permissions);
    }
}
