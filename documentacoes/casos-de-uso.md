# Documento de Casos de Uso — Plataforma Cardápio Digital

## 1. Objetivo
Descrever os principais casos de uso da plataforma Cardápio Digital, considerando os atores, regras de negócio e fluxo técnico definidos nos documentos de escopo, visão e requisitos.

## 2. Atores
- **Administrador de Plataforma**
- **Administrador de Cardápio**
- **Visitante/Cliente**
- **Sistema de Agendamento (interno)** para alternância automática do Happy Hour (HH)

## 3. Regras de Negócio Aplicáveis
- O cadastro inicial de novo estabelecimento deve ser transacional: estabelecimento + cardápio vazio + primeiro Administrador de Cardápio.
- O cadastro de Administrador de Cardápio para estabelecimento existente usa `estabelecimento_id` interno já cadastrado.
- Um estabelecimento pode ter zero, um ou N Administradores de Cardápio.
- Um Administrador de Cardápio pertence a um e somente um estabelecimento.
- Administrador de Cardápio não pode acessar dados de outros estabelecimentos.
- Cada estabelecimento possui no máximo um item de HH por vez, com preços promocionais versionados.
- A alternância entre cardápio comum e HH é automática por agenda configurada e estado do HH.

## 4. Casos de Uso

### UC-01 — Autenticar usuário administrativo
- **Ator principal:** Administrador de Plataforma / Administrador de Cardápio
- **Pré-condições:** Usuário cadastrado e ativo.
- **Fluxo principal:**
1. Usuário informa credenciais.
2. Sistema valida credenciais e perfil.
3. Sistema libera acesso ao módulo correspondente.
- **Fluxo alternativo:** Credenciais inválidas → acesso negado.
- **Pós-condições:** Sessão autenticada com permissões do perfil.

### UC-02 — Cadastrar primeiro Administrador de Cardápio para novo estabelecimento
- **Ator principal:** Administrador de Plataforma
- **Pré-condições:** Administrador de Plataforma autenticado.
- **Fluxo principal:**
1. Administrador escolhe criar novo estabelecimento.
2. Informa dados do estabelecimento e do primeiro usuário administrador de cardápio.
3. Sistema executa operação transacional para gravar estabelecimento, cardápio e usuário.
4. Sistema confirma criação e vínculo do usuário ao `estabelecimento_id`.
- **Fluxo alternativo:** Falha em qualquer gravação da transação → nenhuma entidade é criada.
- **Pós-condições:** Novo estabelecimento com cardápio vazio e primeiro administrador de cardápio vinculado.

### UC-03 — Cadastrar Administrador de Cardápio para estabelecimento existente
- **Ator principal:** Administrador de Plataforma
- **Pré-condições:** Administrador de Plataforma autenticado; estabelecimento já existente.
- **Fluxo principal:**
1. Administrador seleciona `estabelecimento_id` existente.
2. Sistema valida existência do estabelecimento e do cardápio.
3. Sistema grava usuário administrador de cardápio vinculado ao estabelecimento.
- **Fluxo alternativo:** `estabelecimento_id` inválido/inexistente → criação bloqueada.
- **Pós-condições:** Novo administrador de cardápio vinculado sem recriar estabelecimento ou cardápio.

### UC-04 — Listar, atualizar e excluir Administradores de Cardápio
- **Ator principal:** Administrador de Plataforma
- **Pré-condições:** Administrador de Plataforma autenticado.
- **Fluxo principal:**
1. Administrador lista usuários de cardápio.
2. Seleciona um usuário para atualizar ou excluir.
3. Sistema aplica a operação solicitada.
- **Pós-condições:** Cadastro atualizado ou removido conforme ação.

### UC-05 — Gerenciar permissões de usuários administrativos
- **Ator principal:** Administrador de Plataforma
- **Pré-condições:** Administrador de Plataforma autenticado.
- **Fluxo principal:**
1. Administrador seleciona usuário administrativo.
2. Define permissões.
3. Sistema salva alterações e registra auditoria.
- **Pós-condições:** Permissões atualizadas.

### UC-06 — Cadastrar produto no cardápio
- **Ator principal:** Administrador de Cardápio
- **Pré-condições:** Administrador de Cardápio autenticado.
- **Fluxo principal:**
1. Usuário informa nome, preço, descrição/ingredientes, disponibilidade e imagem.
2. Sistema valida obrigatoriedade da imagem e demais campos.
3. Sistema salva produto no cardápio do estabelecimento vinculado ao usuário.
- **Fluxo alternativo:** Imagem ausente/inválida → cadastro não é concluído.
- **Pós-condições:** Produto disponível para exibição conforme disponibilidade.

### UC-07 — Editar, alterar disponibilidade e excluir produto
- **Ator principal:** Administrador de Cardápio
- **Pré-condições:** Produto existente no estabelecimento do usuário.
- **Fluxo principal:**
1. Usuário lista produtos do seu estabelecimento.
2. Seleciona produto para editar, alterar disponibilidade ou excluir.
3. Sistema valida permissão por vínculo e executa ação.
- **Fluxo alternativo:** Produto de outro estabelecimento → acesso negado.
- **Pós-condições:** Cardápio atualizado.

### UC-08 — Visualizar prévia do cardápio
- **Ator principal:** Administrador de Cardápio
- **Pré-condições:** Administrador de Cardápio autenticado.
- **Fluxo principal:**
1. Usuário acessa prévia.
2. Sistema mostra estrutura final do cardápio com dados vigentes.
- **Pós-condições:** Usuário valida apresentação antes da consulta pública.

### UC-09 — Configurar Happy Hour (HH) versionado
- **Ator principal:** Administrador de Cardápio
- **Pré-condições:** Administrador de Cardápio autenticado.
- **Fluxo principal:**
1. Usuário define `inicio_utc` e `fim_utc`.
2. Define preços promocionais por produto participante.
3. Sistema valida `inicio_utc < fim_utc`, cria `versao_id` e salva configuração.
- **Pós-condições:** HH configurado para execução automática.

### UC-10 — Alternar automaticamente estado do Happy Hour
- **Ator principal:** Sistema de Agendamento (interno)
- **Pré-condições:** Agenda de HH configurada para o estabelecimento.
- **Fluxo principal:**
1. No horário de início, sistema tenta transição `AGENDADO -> ATIVO` validando versão e janela.
2. No horário de término, sistema executa transição `AGENDADO/ATIVO -> ENCERRADO`.
3. Atualizações tardias/repetidas são bloqueadas por condições de estado e versão.
- **Pós-condições:** Estado de HH consistente com a janela temporal.

### UC-11 — Consultar cardápio público por link/QR Code
- **Ator principal:** Visitante/Cliente
- **Pré-condições:** Cardápio do estabelecimento publicado.
- **Fluxo principal:**
1. Visitante acessa URL ou lê QR Code.
2. Sistema lê HH com consistência forte e decide exibição.
3. Visitante visualiza nome, preço, ingredientes, disponibilidade e imagem dos produtos.
- **Regra de preço:** preço promocional só quando `status == ATIVO` e `inicio_utc <= agora < fim_utc`.
- **Pós-condições:** Consulta do cardápio concluída.

### UC-12 — Bloquear acesso cruzado entre estabelecimentos
- **Ator principal:** Sistema
- **Pré-condições:** Requisição de usuário autenticado em operação administrativa.
- **Fluxo principal:**
1. Sistema identifica estabelecimento vinculado ao usuário.
2. Compara vínculo do usuário com recurso solicitado.
3. Se houver divergência, bloqueia operação.
- **Pós-condições:** Integridade e isolamento de dados preservados.

## 5. Matriz de rastreabilidade (resumo)
| Caso de Uso | Requisitos relacionados |
|---|---|
| UC-01 | RF-01, RF-02 |
| UC-02 | RF-03, RF-10, RF-11 |
| UC-03 | RF-06, RF-07, RF-09, RF-10 |
| UC-04 | RF-04 |
| UC-05 | RF-05, RNF-06 |
| UC-06 | RF-13, RF-14, RNF-11, RNF-12, RNF-13 |
| UC-07 | RF-15, RF-16, RF-17, RF-18, RF-12 |
| UC-08 | RF-19 |
| UC-09 | RF-25, RF-26, RF-27, RF-28 |
| UC-10 | RF-29, RF-30, RF-31, RNF-17 |
| UC-11 | RF-20, RF-21, RF-22, RF-23, RF-24, RNF-18, RNF-19 |
| UC-12 | RF-12, RNF-04, RNF-05 |

## 6. Observações técnicas
- A persistência segue 6 tabelas DynamoDB: `usuarios`, `estabelecimentos`, `cardapios`, `produtos`, `happy_hours` e `precos_happy_hour`.
- O índice `usuarios.por_estabelecimento` suporta listagem de usuários por estabelecimento.
