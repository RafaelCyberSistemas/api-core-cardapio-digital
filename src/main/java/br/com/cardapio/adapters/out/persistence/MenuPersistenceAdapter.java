package br.com.cardapio.adapters.out.persistence;

import br.com.cardapio.adapters.out.persistence.entity.MenuEntity;
import br.com.cardapio.adapters.out.persistence.repository.MenuJpaRepository;
import br.com.cardapio.application.port.out.MenuRepositoryPort;
import br.com.cardapio.domain.model.Menu;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MenuPersistenceAdapter implements MenuRepositoryPort {
    private final MenuJpaRepository repository;

    public MenuPersistenceAdapter(MenuJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Menu save(Menu menu) {
        MenuEntity entity = new MenuEntity();
        entity.setId(menu.id());
        entity.setEstabelecimentoId(menu.estabelecimentoId());
        MenuEntity saved = repository.save(entity);
        return new Menu(saved.getId(), saved.getEstabelecimentoId());
    }

    @Override
    public boolean existsByEstabelecimentoId(UUID estabelecimentoId) {
        return repository.existsByEstabelecimentoId(estabelecimentoId);
    }
}
