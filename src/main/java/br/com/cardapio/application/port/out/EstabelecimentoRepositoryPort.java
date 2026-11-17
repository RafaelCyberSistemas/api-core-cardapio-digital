package br.com.cardapio.application.port.out;

import br.com.cardapio.domain.model.Estabelecimento;

import java.util.Optional;
import java.util.UUID;

public interface EstabelecimentoRepositoryPort {
    Estabelecimento save(Estabelecimento estabelecimento);

    Optional<Estabelecimento> findById(UUID id);

    boolean existsById(UUID id);
}
