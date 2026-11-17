package br.com.cardapio.application.port.out;

import br.com.cardapio.domain.model.Menu;

import java.util.UUID;

public interface MenuRepositoryPort {
    Menu save(Menu menu);

    boolean existsByEstabelecimentoId(UUID estabelecimentoId);
}
