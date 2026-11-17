package br.com.cardapio.application.service;

import br.com.cardapio.domain.exception.AccessDeniedException;
import br.com.cardapio.infrastructure.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TenantAccessService {
    public void garantirMesmoEstabelecimento(AuthenticatedUser currentUser, UUID estabelecimentoId) {
        if (currentUser.estabelecimentoId() == null || !currentUser.estabelecimentoId().equals(estabelecimentoId)) {
            throw new AccessDeniedException("Acesso cruzado entre estabelecimentos não permitido");
        }
    }
}
