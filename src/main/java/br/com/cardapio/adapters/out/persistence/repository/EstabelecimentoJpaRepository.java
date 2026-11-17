package br.com.cardapio.adapters.out.persistence.repository;

import br.com.cardapio.adapters.out.persistence.entity.EstabelecimentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstabelecimentoJpaRepository extends JpaRepository<EstabelecimentoEntity, UUID> {
}
