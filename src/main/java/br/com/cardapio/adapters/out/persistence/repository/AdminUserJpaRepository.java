package br.com.cardapio.adapters.out.persistence.repository;

import br.com.cardapio.adapters.out.persistence.entity.AdminUserEntity;
import br.com.cardapio.domain.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminUserJpaRepository extends JpaRepository<AdminUserEntity, UUID> {
    Optional<AdminUserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    List<AdminUserEntity> findByRole(UserRole role);

    List<AdminUserEntity> findByEstabelecimentoId(UUID estabelecimentoId);
}
