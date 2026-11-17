package br.com.cardapio.infrastructure.config;

import br.com.cardapio.application.port.out.AdminUserRepositoryPort;
import br.com.cardapio.application.port.out.PasswordHasherPort;
import br.com.cardapio.domain.model.AdminUser;
import br.com.cardapio.domain.model.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.UUID;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedPlatformAdmin(AdminUserRepositoryPort adminUserRepository, PasswordHasherPort passwordHasher) {
        return args -> {
            String email = "platform@cardapio.local";
            if (!adminUserRepository.existsByEmail(email)) {
                adminUserRepository.save(new AdminUser(
                        UUID.randomUUID(),
                        "Administrador Plataforma",
                        email,
                        passwordHasher.hash("Admin@123"),
                        UserRole.ADMIN_PLATAFORMA,
                        null,
                        true,
                        Set.of("USER_MANAGEMENT")
                ));
            }
        };
    }
}
