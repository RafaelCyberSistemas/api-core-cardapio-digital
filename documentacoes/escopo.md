# Escopo do Projeto — Cardápio Digital SaaS

## 1. Visão geral do projeto
Desenvolver uma plataforma SaaS de cardápio digital para bares e restaurantes, permitindo publicar e manter atualizadas as informações de produtos (nome, preço, ingredientes, disponibilidade e imagem obrigatória), com acesso público por **link** e **QR Code**.

## 2. Problema que o projeto resolve
Centralizar e manter disponível, de forma simples e atualizada, o cardápio digital de pequenos e médios estabelecimentos, evitando inconsistências de preço, itens e disponibilidade.

## 3. Objetivo
Entregar uma solução web multiempresa (multi-tenant) com:
- gestão administrativa de produtos por estabelecimento;
- gestão de usuários cliente pela administração da plataforma;
- consulta pública do cardápio em dispositivos móveis e desktop.

---

## 4. Escopo funcional (incluído)

### 4.1 Gestão de acesso e perfis
- Autenticação de usuários administrativos.
- Controle de acesso por perfil:
    - **Administrador da plataforma**.
    - **Administrador de cardápio**.

### 4.2 Gestão de usuários (Administrador da plataforma)
- Criar usuários administradores de cardápio.
- Listar usuários administradores de cardápio.
- Atualizar usuários administradores de cardápio.
- Excluir usuários administradores de cardápio.
- Criar primeiro administrador de cardápio de um novo estabelecimento em operação transacional com:
    - criação do estabelecimento;
    - criação do cardápio vazio;
    - criação do usuário administrador de cardápio vinculado.
- Criar administradores de cardápio adicionais para estabelecimento existente por `estabelecimento_id` interno.
- Listar administradores de cardápio por estabelecimento.
- No cadastro de estabelecimento, os campos cadastrais são validados pelo backend; a modelagem DynamoDB não impõe atributos não-chave.
- Regra de vínculo no cadastro de usuários:
    - um estabelecimento pode ter **zero, um ou N** usuários administradores de cardápio vinculados;
    - um usuário administrador de cardápio pode estar vinculado a **um e somente um** estabelecimento.

### 4.3 Gestão de produtos (Administrador de cardápio)
- Cadastrar produto com campos obrigatórios:
    - nome;
    - preço;
    - descrição/ingredientes;
    - disponibilidade;
    - **imagem obrigatória**.
- Listar produtos do próprio estabelecimento.
- Editar dados de produto (incluindo imagem).
- Alterar disponibilidade (disponível/indisponível).
- Excluir produto.
- Visualizar prévia/estrutura final do cardápio.

### 4.4 Cardápio público
- Página pública por estabelecimento.
- Acesso por URL única.
- Acesso por QR Code vinculado à URL.
- Exibição de:
    - nome do produto;
    - preço;
    - ingredientes;
    - disponibilidade;
    - **imagem do produto**.

### 4.5 Isolamento entre clientes (multi-tenant)
- Cada administrador de cardápio acessa somente os dados do seu estabelecimento.
- Bloqueio total de acesso/alteração de dados entre clientes.

---

## 5. Escopo não funcional (incluído)

- Arquitetura SaaS compartilhada com isolamento lógico por cliente.
- Persistência em DynamoDB com 6 tabelas: `usuarios`, `estabelecimentos`, `cardapios`, `produtos`, `happy_hours`, `precos_happy_hour`.
- Segurança:
    - HTTPS;
    - proteção de senha com hash + salt;
    - controle de sessão;
    - validações no backend.
- Upload de imagens:
    - validação de tipo;
    - validação de tamanho;
    - imagem obrigatória por produto.
- Responsividade (smartphone, tablet, desktop).
- Compatibilidade com navegadores modernos.
- Acessibilidade com meta WCAG 2.2 AA.
- Observabilidade (logs, métricas e alertas).
- Backup e recuperação.
- Auditoria de ações administrativas.
- Metas de desempenho e disponibilidade conforme requisitos definidos.
- Consistência forte na leitura pública da configuração de happy hour.

---

## 6. Regras de negócio principais
- Produto pertence a um único estabelecimento.
- Cada estabelecimento possui um único cardápio (cardápio vazio quando não há produtos).
- Produto **não pode existir sem imagem válida**.
- Apenas administrador da plataforma gerencia usuários de clientes.
- Criação de novo estabelecimento ocorre em transação atômica com estabelecimento + cardápio + primeiro administrador de cardápio.
- Para estabelecimento existente, a criação de administrador de cardápio utiliza `estabelecimento_id` interno existente.
- Um estabelecimento pode ter zero, um ou N administradores de cardápio vinculados.
- Cada administrador de cardápio deve estar vinculado a um e somente um estabelecimento.
- Apenas administrador de cardápio gerencia produtos do próprio estabelecimento.
- Produto indisponível não é excluído automaticamente.
- Cada estabelecimento possui no máximo uma configuração de happy hour por vez.
- Alterações de cardápio não exigem novo link ou novo QR Code.

---

## 7. Entregáveis do projeto
1. Módulo administrativo da plataforma (gestão de usuários cliente).
2. Módulo administrativo do estabelecimento (gestão de produtos com imagem obrigatória).
3. Módulo público de exibição de cardápio.
4. Geração/disponibilização de URL e QR Code por estabelecimento.
5. Documentação funcional e não funcional.
6. Critérios de validação/homologação.
7. (Opcional recomendado) suíte mínima de testes automatizados dos fluxos críticos.

---

## 8. Fora do escopo (não incluído nesta fase)
- Pedidos online.
- Pagamentos.
- Controle de estoque quantitativo.
- Delivery/logística.
- Reserva de mesas.
- Programa de fidelidade.
- Integrações com ERP/PDV.
- Múltiplas imagens por produto (além da imagem principal obrigatória).

---

## 9. Premissas
- Cada estabelecimento poderá ter zero, um ou N administradores de cardápio.
- O cardápio será acessado por navegador (sem app nativo obrigatório).
- O estabelecimento será responsável por manter produtos e imagens atualizados.
- A infraestrutura suportará múltiplos clientes simultâneos.

## 10. Restrições
- Dependência de conexão com internet para acesso ao cardápio.
- Limites técnicos de upload de imagem (formato/tamanho/resolução) conforme configuração final.
- Operação inicial restrita ao escopo de publicação e gestão de cardápio digital.

---

Se quiser, no próximo passo eu transformo este escopo em um **`escopo-projeto.md` final em formato mais executivo** (com seção de aprovação e versão do documento), pronto para anexar em proposta comercial ou abertura de projeto.