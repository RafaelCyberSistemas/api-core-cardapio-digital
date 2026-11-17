package br.com.cardapio.application.service;

import br.com.cardapio.application.port.out.AdminUserRepositoryPort;
import br.com.cardapio.application.port.out.EstabelecimentoRepositoryPort;
import br.com.cardapio.application.port.out.MenuRepositoryPort;
import br.com.cardapio.application.port.out.PasswordHasherPort;
import br.com.cardapio.domain.exception.BusinessException;
import br.com.cardapio.domain.exception.NotFoundException;
import br.com.cardapio.domain.model.AdminUser;
import br.com.cardapio.domain.model.Endereco;
import br.com.cardapio.domain.model.Estabelecimento;
import br.com.cardapio.domain.model.Menu;
import br.com.cardapio.domain.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PlatformAdminUserService {
    private static final Logger log = LoggerFactory.getLogger(PlatformAdminUserService.class);

    private final AdminUserRepositoryPort adminUserRepository;
    private final EstabelecimentoRepositoryPort estabelecimentoRepository;
    private final MenuRepositoryPort menuRepository;
    private final PasswordHasherPort passwordHasher;

    public PlatformAdminUserService(
            AdminUserRepositoryPort adminUserRepository,
            EstabelecimentoRepositoryPort estabelecimentoRepository,
            MenuRepositoryPort menuRepository,
            PasswordHasherPort passwordHasher
    ) {
        this.adminUserRepository = adminUserRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.menuRepository = menuRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public AdminUser provisionarComNovoEstabelecimento(
            String nomeRazaoSocial,
            String cpfCnpj,
            String nomeResponsavel,
            String telefone,
            String whatsapp,
            Endereco endereco,
            String publicSlug,
            String nomeAdmin,
            String emailAdmin,
            String senhaAdmin
    ) {
        validarEmailUnico(emailAdmin);

        UUID estabelecimentoId = UUID.randomUUID();
        estabelecimentoRepository.save(new Estabelecimento(
                estabelecimentoId,
                nomeRazaoSocial,
                cpfCnpj,
                nomeResponsavel,
                telefone,
                whatsapp,
                endereco,
                publicSlug
        ));
        menuRepository.save(new Menu(UUID.randomUUID(), estabelecimentoId));

        AdminUser user = new AdminUser(
                UUID.randomUUID(),
                nomeAdmin,
                emailAdmin,
                passwordHasher.hash(senhaAdmin),
                UserRole.ADMIN_CARDAPIO,
                estabelecimentoId,
                true,
                Set.of()
        );
        AdminUser saved = adminUserRepository.save(user);
        log.info("Auditoria: provisionamento transacional de estabelecimento {} e usuário {}", estabelecimentoId, saved.getId());
        return saved;
    }

    @Transactional
    public AdminUser createAdminUser(
            String name,
            String email,
            String rawPassword,
            UserRole role,
            UUID estabelecimentoId
    ) {
        if (role == UserRole.VISITANTE_CLIENTE) {
            throw new BusinessException("Perfil VISITANTE_CLIENTE não é usuário administrativo");
        }
        validarEmailUnico(email);
        if (role == UserRole.ADMIN_CARDAPIO) {
            if (estabelecimentoId == null) {
                throw new BusinessException("ADMIN_CARDAPIO deve receber estabelecimento_id");
            }
            validarEstabelecimentoECardapioExistentes(estabelecimentoId);
        } else {
            estabelecimentoId = null;
        }
        AdminUser user = new AdminUser(
                UUID.randomUUID(),
                name,
                email,
                passwordHasher.hash(rawPassword),
                role,
                estabelecimentoId,
                true,
                Set.of()
        );
        return adminUserRepository.save(user);
    }

    public List<AdminUser> listMenuAdmins() {
        return adminUserRepository.findByRole(UserRole.ADMIN_CARDAPIO.name());
    }

    public AdminUser updateMenuAdmin(UUID id, String name, boolean active) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        if (user.getRole() != UserRole.ADMIN_CARDAPIO) {
            throw new BusinessException("Apenas ADMIN_CARDAPIO pode ser alterado nesta operação");
        }
        user.setName(name);
        user.setActive(active);
        return adminUserRepository.save(user);
    }

    public void deleteMenuAdmin(UUID id) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        if (user.getRole() != UserRole.ADMIN_CARDAPIO) {
            throw new BusinessException("Apenas ADMIN_CARDAPIO pode ser removido nesta operação");
        }
        adminUserRepository.deleteById(id);
    }

    public AdminUser replacePermissions(UUID id, Set<String> permissions) {
        AdminUser user = findAdminUser(id);
        user.replacePermissions(permissions);
        log.info("Auditoria: substituição de permissões do usuário {}", id);
        return adminUserRepository.save(user);
    }

    public AdminUser addPermission(UUID id, String permission) {
        AdminUser user = findAdminUser(id);
        user.addPermission(permission);
        log.info("Auditoria: inclusão de permissão '{}' para {}", permission, id);
        return adminUserRepository.save(user);
    }

    public AdminUser removePermission(UUID id, String permission) {
        AdminUser user = findAdminUser(id);
        user.removePermission(permission);
        log.info("Auditoria: remoção de permissão '{}' para {}", permission, id);
        return adminUserRepository.save(user);
    }

    public List<AdminUser> listarPorEstabelecimento(UUID estabelecimentoId) {
        return adminUserRepository.findByEstabelecimentoId(estabelecimentoId);
    }

    private AdminUser findAdminUser(UUID id) {
        return adminUserRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    private void validarEmailUnico(String email) {
        if (adminUserRepository.existsByEmail(email.toLowerCase())) {
            throw new BusinessException("E-mail já cadastrado");
        }
    }

    private void validarEstabelecimentoECardapioExistentes(UUID estabelecimentoId) {
        if (!estabelecimentoRepository.existsById(estabelecimentoId)) {
            throw new NotFoundException("Estabelecimento não encontrado");
        }
        if (!menuRepository.existsByEstabelecimentoId(estabelecimentoId)) {
            throw new BusinessException("Cardápio do estabelecimento não encontrado");
        }
    }
}
