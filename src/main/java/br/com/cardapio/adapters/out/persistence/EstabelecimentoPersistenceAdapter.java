package br.com.cardapio.adapters.out.persistence;

import br.com.cardapio.adapters.out.persistence.entity.EnderecoEntity;
import br.com.cardapio.adapters.out.persistence.entity.EstabelecimentoEntity;
import br.com.cardapio.adapters.out.persistence.repository.EstabelecimentoJpaRepository;
import br.com.cardapio.application.port.out.EstabelecimentoRepositoryPort;
import br.com.cardapio.domain.model.Endereco;
import br.com.cardapio.domain.model.Estabelecimento;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class EstabelecimentoPersistenceAdapter implements EstabelecimentoRepositoryPort {
    private final EstabelecimentoJpaRepository repository;

    public EstabelecimentoPersistenceAdapter(EstabelecimentoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Estabelecimento save(Estabelecimento estabelecimento) {
        EstabelecimentoEntity entity = toEntity(estabelecimento);
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<Estabelecimento> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    private EstabelecimentoEntity toEntity(Estabelecimento estabelecimento) {
        EstabelecimentoEntity entity = new EstabelecimentoEntity();
        entity.setId(estabelecimento.id());
        entity.setNomeRazaoSocial(estabelecimento.nomeRazaoSocial());
        entity.setCpfCnpj(estabelecimento.cpfCnpj());
        entity.setNomeResponsavel(estabelecimento.nomeResponsavel());
        entity.setTelefone(estabelecimento.telefone());
        entity.setWhatsapp(estabelecimento.whatsapp());
        entity.setPublicSlug(estabelecimento.publicSlug());

        EnderecoEntity enderecoEntity = new EnderecoEntity();
        enderecoEntity.setLogradouro(estabelecimento.endereco().logradouro());
        enderecoEntity.setBairro(estabelecimento.endereco().bairro());
        enderecoEntity.setCidade(estabelecimento.endereco().cidade());
        enderecoEntity.setCodigoPostal(estabelecimento.endereco().codigoPostal());
        entity.setEndereco(enderecoEntity);
        return entity;
    }

    private Estabelecimento toDomain(EstabelecimentoEntity entity) {
        Endereco endereco = new Endereco(
                entity.getEndereco().getLogradouro(),
                entity.getEndereco().getBairro(),
                entity.getEndereco().getCidade(),
                entity.getEndereco().getCodigoPostal()
        );
        return new Estabelecimento(
                entity.getId(),
                entity.getNomeRazaoSocial(),
                entity.getCpfCnpj(),
                entity.getNomeResponsavel(),
                entity.getTelefone(),
                entity.getWhatsapp(),
                endereco,
                entity.getPublicSlug()
        );
    }
}
