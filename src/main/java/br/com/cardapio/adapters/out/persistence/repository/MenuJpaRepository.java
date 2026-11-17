package br.com.cardapio.adapters.out.persistence.repository;

import br.com.cardapio.adapters.out.persistence.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuJpaRepository extends JpaRepository<MenuEntity, UUID> {
    boolean existsByEstabelecimentoId(UUID estabelecimentoId);
}
