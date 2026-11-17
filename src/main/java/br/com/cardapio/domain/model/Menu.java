package br.com.cardapio.domain.model;

import java.util.Objects;
import java.util.UUID;

public record Menu(UUID id, UUID estabelecimentoId) {
    public Menu {
        Objects.requireNonNull(id);
        Objects.requireNonNull(estabelecimentoId);
    }
}
