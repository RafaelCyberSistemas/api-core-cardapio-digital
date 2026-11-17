package br.com.cardapio.application.port.out;

import br.com.cardapio.domain.model.AdminUser;

public interface TokenProviderPort {
    String generate(AdminUser user);
}
