package br.com.cardapio.application.service;

import br.com.cardapio.application.dto.AuthToken;
import br.com.cardapio.application.port.out.AdminUserRepositoryPort;
import br.com.cardapio.application.port.out.PasswordHasherPort;
import br.com.cardapio.application.port.out.TokenProviderPort;
import br.com.cardapio.domain.exception.BusinessException;
import br.com.cardapio.domain.model.AdminUser;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AdminUserRepositoryPort adminUserRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenProviderPort tokenProvider;

    public AuthService(
            AdminUserRepositoryPort adminUserRepository,
            PasswordHasherPort passwordHasher,
            TokenProviderPort tokenProvider
    ) {
        this.adminUserRepository = adminUserRepository;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
    }

    public AuthToken login(String email, String password) {
        AdminUser user = adminUserRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas"));
        if (!user.isActive() || !passwordHasher.matches(password, user.getPasswordHash())) {
            throw new BusinessException("Credenciais inválidas");
        }
        String token = tokenProvider.generate(user);
        return new AuthToken(token, user.getId(), user.getEmail(), user.getRole(), user.getEstabelecimentoId(), user.getPermissions());
    }
}
