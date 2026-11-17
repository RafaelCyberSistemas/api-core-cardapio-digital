package br.com.cardapio.adapters.out.persistence;

import br.com.cardapio.adapters.out.persistence.entity.AdminUserEntity;
import br.com.cardapio.adapters.out.persistence.repository.AdminUserJpaRepository;
import br.com.cardapio.application.port.out.AdminUserRepositoryPort;
import br.com.cardapio.domain.model.AdminUser;
import br.com.cardapio.domain.model.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AdminUserPersistenceAdapter implements AdminUserRepositoryPort {

    private final AdminUserJpaRepository repository;

    public AdminUserPersistenceAdapter(AdminUserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public AdminUser save(AdminUser user) {
        AdminUserEntity entity = toEntity(user);
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<AdminUser> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<AdminUser> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<AdminUser> findByRole(String role) {
        return repository.findByRole(UserRole.valueOf(role)).stream().map(this::toDomain).toList();
    }

    @Override
    public List<AdminUser> findByEstabelecimentoId(UUID estabelecimentoId) {
        return repository.findByEstabelecimentoId(estabelecimentoId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private AdminUserEntity toEntity(AdminUser user) {
        AdminUserEntity entity = new AdminUserEntity();
        entity.setId(user.getId());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setRole(user.getRole());
        entity.setEstabelecimentoId(user.getEstabelecimentoId());
        entity.setActive(user.isActive());
        entity.setPermissions(user.getPermissions());
        return entity;
    }

    private AdminUser toDomain(AdminUserEntity entity) {
        return new AdminUser(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.getEstabelecimentoId(),
                entity.isActive(),
                entity.getPermissions()
        );
    }
}
