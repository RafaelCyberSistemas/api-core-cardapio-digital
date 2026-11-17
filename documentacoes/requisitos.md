# Documento de Requisitos — Cardápio Digital

## 1. Objetivo
Definir os requisitos funcionais e não funcionais da plataforma Cardápio Digital, com base nos documentos de escopo e visão/termo de abertura.

## 2. Requisitos Funcionais (RF)

### 2.1 Acesso, perfis e usuários
- **RF-01:** O sistema deve permitir autenticação de usuários administrativos.
- **RF-02:** O sistema deve possuir os perfis: Administrador de Plataforma, Administrador de Cardápio e Visitante/Cliente.
- **RF-03:** O Administrador de Plataforma deve poder criar usuário Administrador de Cardápio para novo estabelecimento em operação transacional atômica que grave estabelecimento, cardápio e usuário.
- **RF-04:** O Administrador de Plataforma deve poder listar, atualizar e excluir usuários Administradores de Cardápio.
- **RF-05:** O Administrador de Plataforma deve poder criar, alterar, adicionar e remover permissões de usuários administrativos.
- **RF-06:** O Administrador de Plataforma deve poder criar usuário Administrador de Cardápio para estabelecimento existente selecionando `estabelecimento_id` interno.
- **RF-07:** Ao criar usuário para estabelecimento existente, o sistema deve validar a existência de estabelecimento e cardápio antes de gravar o vínculo.
- **RF-08:** O sistema deve permitir criação de usuário `ADMIN_PLATAFORMA` sem vínculo com estabelecimento.
- **RF-09:** Um estabelecimento pode possuir zero, um ou N usuários Administradores de Cardápio vinculados.
- **RF-10:** Cada usuário Administrador de Cardápio deve estar vinculado a um e somente um `estabelecimento_id`.
- **RF-11:** O sistema deve permitir consulta de administradores por estabelecimento via índice secundário global.
- **RF-12:** Deve ser estritamente proibido que um Administrador de Cardápio acesse, visualize, crie, edite, atualize ou exclua dados de outros cardápios.

### 2.2 Gestão de produtos (Administrador de Cardápio)
- **RF-13:** O Administrador de Cardápio deve poder cadastrar produtos do seu próprio estabelecimento.
- **RF-14:** O cadastro de produto deve exigir, no mínimo: nome, preço, descrição/ingredientes, disponibilidade e imagem.
- **RF-15:** O Administrador de Cardápio deve poder listar apenas os produtos do seu estabelecimento.
- **RF-16:** O Administrador de Cardápio deve poder editar produtos do seu estabelecimento, incluindo imagem.
- **RF-17:** O Administrador de Cardápio deve poder alterar disponibilidade de produtos (disponível/indisponível) do seu estabelecimento.
- **RF-18:** O Administrador de Cardápio deve poder excluir produtos do seu estabelecimento.
- **RF-19:** O sistema deve permitir visualização prévia da estrutura final do cardápio.

### 2.3 Cardápio público
- **RF-20:** O sistema deve disponibilizar uma página pública de cardápio para cada estabelecimento.
- **RF-21:** O acesso ao cardápio público deve ocorrer por URL única.
- **RF-22:** O sistema deve permitir acesso ao cardápio por QR Code vinculado à URL do estabelecimento.
- **RF-23:** O cardápio público deve exibir, por produto: nome, preço, ingredientes, disponibilidade e imagem.
- **RF-24:** O visitante deve conseguir acessar o cardápio em celular, tablet, notebook e outros dispositivos com navegador.

### 2.4 Funcionalidade Happy Hour (HH)
- **RF-25:** Cada estabelecimento deve possuir no máximo uma configuração de Happy Hour (HH) por vez.
- **RF-26:** A configuração de HH deve armazenar `versao_id`, `inicio_utc`, `fim_utc` e `status`.
- **RF-27:** O backend deve validar `inicio_utc < fim_utc` antes de publicar uma configuração de HH.
- **RF-28:** Os preços promocionais devem ser gravados por `versao_id` e `produto_id`, preservando `preco_normal_centavos` no produto.
- **RF-29:** A ativação automática deve executar transição `AGENDADO -> ATIVO` no início e o encerramento `AGENDADO/ATIVO -> ENCERRADO` no fim, com validação de versão e estado.
- **RF-30:** Na leitura pública, o preço promocional só pode ser aplicado quando `status == ATIVO` e `inicio_utc <= agora < fim_utc`.
- **RF-31:** A mudança entre cardápio comum e HH deve manter o mesmo link e QR Code e não impactar outros estabelecimentos.

## 3. Requisitos Não Funcionais (RNF)

### 3.1 Segurança e isolamento de dados
- **RNF-01:** O sistema deve utilizar HTTPS para tráfego de dados.
- **RNF-02:** Senhas devem ser armazenadas com hash e salt.
- **RNF-03:** O sistema deve aplicar controle de acesso por perfil (RBAC).
- **RNF-04:** O sistema deve garantir isolamento lógico multi-tenant entre estabelecimentos.
- **RNF-05:** O sistema deve impedir acesso cruzado entre cardápios em todas as operações (leitura e escrita).
- **RNF-06:** O sistema deve manter auditoria das ações administrativas críticas.
- **RNF-07:** O backend deve validar entradas de dados antes de persistência/processamento.

### 3.2 Usabilidade e compatibilidade
- **RNF-08:** A interface pública e administrativa deve ser responsiva para smartphone, tablet e desktop.
- **RNF-09:** O sistema deve ser compatível com navegadores modernos.
- **RNF-10:** O sistema deve buscar conformidade com acessibilidade em nível WCAG 2.2 AA.

### 3.3 Imagens e mídia
- **RNF-11:** O upload de imagem deve validar tipo de arquivo.
- **RNF-12:** O upload de imagem deve validar tamanho do arquivo.
- **RNF-13:** Produto sem imagem válida não deve ser publicado no cardápio.

### 3.4 Confiabilidade e operação
- **RNF-14:** O sistema deve possuir mecanismos de logs, métricas e alertas.
- **RNF-15:** O sistema deve possuir estratégia de backup e recuperação.
- **RNF-16:** A plataforma deve suportar operação multiempresa simultânea com segregação de dados.
- **RNF-17:** A alternância automática entre cardápio comum e HH deve ocorrer de forma confiável conforme agenda configurada.
- **RNF-18:** A leitura da configuração de HH no fluxo público deve usar leitura consistente forte (`ConsistentRead = true`).
- **RNF-19:** O cache da resposta pública não pode manter preços promocionais após `fim_utc`.

## 4. Fora de Escopo desta Fase
- Pedidos online.
- Pagamentos.
- Controle de estoque quantitativo.
- Delivery/logística.
- Reserva de mesas.
- Programa de fidelidade.
- Integrações com ERP/PDV.
