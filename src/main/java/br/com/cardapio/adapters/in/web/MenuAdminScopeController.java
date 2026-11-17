package br.com.cardapio.adapters.in.web;

import br.com.cardapio.application.service.TenantAccessService;
import br.com.cardapio.infrastructure.security.AuthenticatedUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/menu-admin")
public class MenuAdminScopeController {

    private final TenantAccessService tenantAccessService;

    public MenuAdminScopeController(TenantAccessService tenantAccessService) {
        this.tenantAccessService = tenantAccessService;
    }

    @GetMapping("/estabelecimentos/{estabelecimentoId}/scope-check")
    @PreAuthorize("hasRole('ADMIN_CARDAPIO')")
    public Map<String, Object> verificarEscopo(
            @PathVariable UUID estabelecimentoId,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        tenantAccessService.garantirMesmoEstabelecimento(user, estabelecimentoId);
        return Map.of(
                "status", "ok",
                "estabelecimentoId", estabelecimentoId,
                "userId", user.userId()
        );
    }
}
