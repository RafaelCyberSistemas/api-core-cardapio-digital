# User Stories e Backlog do Produto — Cardápio Digital

## 1. User Stories

### Epic A — Acesso, perfis e segurança multi-tenant

**US-01**  
Como **Administrador de Plataforma**, quero **autenticar no sistema**, para **acessar as funções administrativas com segurança**.

**Critérios de aceitação**
- Dado um usuário administrativo válido, quando informar credenciais corretas, então o acesso é permitido.
- Dado credenciais inválidas, quando tentar autenticar, então o acesso é negado.

**US-02**  
Como **Administrador de Plataforma**, quero **criar o primeiro administrador de cardápio de um novo estabelecimento**, para **iniciar a operação do cliente com cardápio vazio já provisionado**.

**Critérios de aceitação**
- A criação grava estabelecimento, cardápio e usuário em transação única.
- Se qualquer gravação falhar, nenhuma alteração é persistida.
- O usuário de cardápio é criado com vínculo a um único `estabelecimento_id`.

**US-03**  
Como **Administrador de Plataforma**, quero **cadastrar administrador de cardápio para estabelecimento já existente**, para **adicionar novos gestores sem recriar estabelecimento ou cardápio**.

**Critérios de aceitação**
- O fluxo exige seleção de `estabelecimento_id` interno existente.
- O sistema valida existência do estabelecimento e cardápio antes de criar o usuário.
- Um estabelecimento pode possuir zero, um ou N administradores de cardápio vinculados.

**US-04**  
Como **Administrador de Plataforma**, quero **gerenciar permissões e dados de usuários administrativos**, para **manter governança e controle de acesso**.

**Critérios de aceitação**
- O Administrador de Plataforma pode listar, editar, ativar/desativar e remover usuários administrativos.
- A operação deve registrar auditoria.

**US-05**  
Como **Administrador de Cardápio**, quero **acessar somente meu cardápio**, para **garantir segurança e privacidade dos dados de outros estabelecimentos**.

**Critérios de aceitação**
- O usuário só visualiza dados do estabelecimento ao qual está vinculado.
- Qualquer tentativa de acesso a outro cardápio é bloqueada.

### Epic B — Gestão de produtos

**US-06**  
Como **Administrador de Cardápio**, quero **cadastrar produtos**, para **publicar itens no cardápio digital**.

**Critérios de aceitação**
- Campos obrigatórios: nome, preço, descrição/ingredientes, disponibilidade e imagem.
- Produto sem imagem válida não pode ser salvo/publicado.

**US-07**  
Como **Administrador de Cardápio**, quero **editar e excluir produtos do meu estabelecimento**, para **manter o cardápio atualizado**.

**Critérios de aceitação**
- Edição e exclusão são permitidas apenas para produtos do estabelecimento vinculado.
- Atualizações devem refletir no cardápio público.

**US-08**  
Como **Administrador de Cardápio**, quero **alterar disponibilidade dos produtos**, para **controlar o que pode ser exibido ao cliente**.

**Critérios de aceitação**
- Produto pode ser marcado como disponível/indisponível.
- Produto indisponível permanece cadastrado.

### Epic C — Cardápio público e acesso via QR Code

**US-09**  
Como **Visitante**, quero **acessar o cardápio por link ou QR Code**, para **consultar os produtos do estabelecimento rapidamente**.

**Critérios de aceitação**
- Cada estabelecimento possui URL única.
- O QR Code direciona para a URL do cardápio do estabelecimento.

**US-10**  
Como **Visitante**, quero **visualizar nome, preço, ingredientes, disponibilidade e imagem dos produtos**, para **tomar decisão de consumo**.

**Critérios de aceitação**
- O cardápio público exibe as informações de produto definidas nos requisitos.
- A visualização funciona em celular, tablet e desktop.

### Epic D — Happy Hour (HH)

**US-11**  
Como **Administrador de Cardápio**, quero **configurar Happy Hour com janela UTC e versão**, para **automatizar a troca do cardápio comum para HH**.

**Critérios de aceitação**
- Configuração utiliza `inicio_utc`, `fim_utc`, `versao_id` e `status`.
- O sistema valida `inicio_utc < fim_utc`.
- A configuração é aplicada somente ao cardápio do estabelecimento vinculado.

**US-12**  
Como **Administrador de Cardápio**, quero **definir preços e disponibilidades específicas para HH**, para **oferecer condições promocionais no período**.

**Critérios de aceitação**
- Produtos no HH têm preço promocional por `versao_id` e `produto_id`.
- Fora do horário HH, prevalecem dados do cardápio comum.

**US-13**  
Como **Visitante**, quero **ver automaticamente o cardápio HH no período configurado e o cardápio comum fora dele**, para **consultar sempre o cardápio vigente**.

**Critérios de aceitação**
- Promoção só aparece quando `status == ATIVO` e `inicio_utc <= agora < fim_utc`.
- No horário de término, novas leituras retornam ao preço normal.
- Não há troca de URL nem de QR Code.

---

## 2. Backlog do Produto (priorizado)

| ID | Item do Backlog | Tipo | Prioridade | User Story(s) | Critério de pronto (resumo) |
|---|---|---|---|---|---|
| PB-01 | Autenticação administrativa | Funcionalidade | Must | US-01 | Login seguro com validação de credenciais |
| PB-02 | Gestão de usuários administrativos | Funcionalidade | Must | US-02, US-04 | CRUD de usuários e permissões com auditoria |
| PB-03 | Cadastro transacional inicial | Funcionalidade | Must | US-02 | Criar estabelecimento, cardápio e primeiro administrador em transação atômica |
| PB-04 | Vínculo em estabelecimento existente | Funcionalidade | Must | US-03 | Criar administrador de cardápio adicional por `estabelecimento_id` sem recriação de cardápio |
| PB-05 | Isolamento multi-tenant em leitura/escrita | Segurança | Must | US-05, US-07 | Bloqueio total de acesso cruzado entre cardápios |
| PB-06 | Cadastro de produtos com imagem obrigatória | Funcionalidade | Must | US-06 | Validação completa dos campos obrigatórios |
| PB-07 | Edição, listagem, exclusão e disponibilidade de produtos | Funcionalidade | Must | US-07, US-08 | Operações limitadas ao próprio estabelecimento |
| PB-08 | Página pública de cardápio por estabelecimento | Funcionalidade | Must | US-09, US-10 | Exibição pública com URL única |
| PB-09 | Acesso por QR Code | Funcionalidade | Must | US-09 | QR Code direciona para URL do cardápio |
| PB-10 | Exibição responsiva em múltiplos dispositivos | Não funcional | Must | US-10 | Interface funcional em smartphone, tablet e desktop |
| PB-11 | Configuração versionada de Happy Hour | Funcionalidade | Must | US-11 | Persistir `versao_id`, `inicio_utc`, `fim_utc` e `status` por estabelecimento |
| PB-12 | Tabela de preços promocionais | Funcionalidade | Must | US-12 | Persistir preços por `versao_id` e `produto_id`, sem sobrescrever preço normal |
| PB-13 | Motor de transição de estados HH | Funcionalidade | Must | US-13 | Executar transições de estado por janela e versão sem novo link/QR |
| PB-14 | Auditoria de ações administrativas críticas | Não funcional | Should | US-04 | Registro de operações de gestão |
| PB-15 | Observabilidade (logs, métricas e alertas) | Não funcional | Should | US-01 a US-13 | Monitoramento operacional básico ativo |
| PB-16 | Backup e recuperação | Não funcional | Should | US-01 a US-13 | Rotina de backup e procedimento de restore definidos |
| PB-17 | Acessibilidade alvo WCAG 2.2 AA | Não funcional | Could | US-10 | Ajustes mínimos de acessibilidade implementados |

---

## 3. Sugestão de roadmap por release

### Release 1 (MVP)
- PB-01 a PB-10

### Release 2
- PB-11 a PB-13

### Release 3
- PB-14 a PB-17
