# Documento de Visão e Termo de Abertura do Projeto

## 1. Identificação do Projeto
- **Nome do projeto:** Cardápio Digital
- **Tipo de solução:** Plataforma web SaaS (multi-tenant)
- **Segmento-alvo:** Pequenas e médias empresas de bares e restaurantes

## 2. Contexto e Justificativa
O projeto Cardápio Digital nasce da necessidade de oferecer uma forma simples, rápida e atualizável de disponibilizar cardápios ao 
público por meio de **QR Code** ou **link direto**. A solução reduz problemas de desatualização de informações, 
melhora a experiência do cliente e facilita a gestão de produtos por cada estabelecimento.

## 3. Objetivo Geral
Disponibilizar uma plataforma de cardápio digital em que cada estabelecimento tenha seu cardápio individual, 
com gestão própria de produtos e acesso público por diferentes dispositivos digitais (celular, tablet, notebook e similares).

## 4. Objetivos Específicos
1. Permitir publicação de cardápio digital por estabelecimento com URL única e QR Code.
2. Garantir gestão segura de produtos por usuário autorizado do próprio estabelecimento.
3. Permitir gestão de usuários e permissões pela administração da plataforma.
4. Oferecer consulta pública responsiva e simples para visitantes/clientes.

## 5. Escopo do Projeto

### 5.1 Escopo funcional
- Cadastro, edição, listagem e exclusão de produtos por estabelecimento.
- Exibição pública do cardápio por link e QR Code.
- Administração de usuários e permissões pela plataforma.
- Isolamento de dados entre estabelecimentos.

### 5.2 Escopo não funcional
- Segurança de acesso e autenticação.
- Isolamento lógico multi-tenant.
- Responsividade para diferentes dispositivos.
- Validações de dados no backend.

## 6. Atores e Responsabilidades

### 6.1 Administrador de Cardápio
Usuário vinculado a um estabelecimento específico, responsável por inserir e manter informações dos produtos do 
cardápio digital do seu próprio estabelecimento.

**Regra crítica de acesso:** é estritamente proibido criar, atualizar, editar ou remover produtos de cardápios 
de outros estabelecimentos. Esse usuário só pode interagir com dados do estabelecimento ao qual está vinculado.

### 6.2 Administrador de Plataforma
Ator responsável pela administração de usuários da solução, incluindo:
- criar usuários do tipo administrador de cardápio;
- criar, alterar, adicionar permissões e remover permissões;
- criar o primeiro administrador de cardápio de um novo estabelecimento em operação transacional com criação do estabelecimento e do cardápio vazio;
- criar administradores de cardápio adicionais para estabelecimentos existentes por `estabelecimento_id`;
- executar atividades de manutenção de usuários administrativos da plataforma e de cardápio.

**Regra de vínculo:** um estabelecimento pode ter zero, um ou N administradores de cardápio; cada administrador de cardápio pertence a um e somente um estabelecimento. Usuários `ADMIN_PLATAFORMA` não possuem vínculo com estabelecimento.

### 6.3 Visitante/Cliente
Pessoa que acessa o cardápio digital por meio de celular, tablet, notebook ou outro dispositivo com navegador, 
utilizando QR Code ou link compartilhado pelo estabelecimento.

## 7. Premissas
- Cada estabelecimento terá cardápio digital individual.
- Cada estabelecimento poderá ter zero, um ou N administradores de cardápio.
- A atualização do cardápio será responsabilidade do próprio estabelecimento.
- O acesso público ocorrerá por navegador web.

## 8. Restrições
- Dependência de internet para acesso ao cardápio.
- Proibição de acesso cruzado entre estabelecimentos.
- Escopo da fase atual restrito à exposição e gestão de cardápio digital (sem pedidos e pagamentos).

## 9. Requisitos de Segurança e Governança
- Controle de acesso por perfil (RBAC).
- Isolamento de dados por estabelecimento.
- Auditoria de ações administrativas críticas.
- Proteção de credenciais e validações de entrada.

## 10. Entregáveis Principais
1. Módulo administrativo da plataforma (gestão de usuários e permissões).
2. Módulo administrativo do estabelecimento (gestão de produtos).
3. Módulo público de exibição do cardápio.
4. Disponibilização de link e QR Code por estabelecimento.
5. Documentação funcional e técnica do produto.

## 11. Critérios de Sucesso
- Estabelecimentos conseguem publicar e manter cardápio sem apoio técnico contínuo.
- Visitantes acessam cardápios com facilidade por QR Code/link em múltiplos dispositivos.
- Controle de acesso impede operações fora do escopo de cada perfil.
- Plataforma suporta múltiplos estabelecimentos com separação segura de dados.

## 12. Aprovação e Abertura
Este documento formaliza a visão do produto e a abertura do projeto Cardápio Digital, alinhado ao escopo definido em `documentacoes/escopo.md`.
