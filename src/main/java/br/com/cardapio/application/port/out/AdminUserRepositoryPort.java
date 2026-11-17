package br.com.cardapio.application.port.out;

import br.com.cardapio.domain.model.AdminUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminUserRepositoryPort {
    AdminUser save(AdminUser user);

    Optional<AdminUser> findByEmail(String email);

    Optional<AdminUser> findById(UUID id);

    List<AdminUser> findByRole(String role);

    List<AdminUser> findByEstabelecimentoId(UUID estabelecimentoId);

    boolean existsByEmail(String email);

    void deleteById(UUID id);
}
