# Arquitetura de Software — Plataforma Cardápio Digital

## 1. Objetivo
Definir a arquitetura da plataforma Cardápio Digital como **API RESTful** com **Java 25 + Spring Boot**, seguindo **Arquitetura Hexagonal (Ports and Adapters)**, princípios de **Clean Architecture** e práticas de **Clean Code**, com persistência em **AWS DynamoDB**, imagens em **AWS S3** e frontend em **React**.

## 2. Direcionadores de Arquitetura
- Multi-tenant com isolamento lógico por estabelecimento.
- Segurança por perfil (`ADMIN_PLATAFORMA`, `ADMIN_CARDAPIO`, visitante público).
- Escalabilidade horizontal para leitura pública de cardápios.
- Consistência das regras críticas (vínculo de usuários, transações de cadastro inicial, Happy Hour).
- Baixo acoplamento entre domínio e infraestrutura.

## 3. Visão de Contexto (C4 - Nível 1)
- **Frontend React** consome API administrativa e pública.
- **API Cardápio Digital (Spring Boot)** expõe endpoints REST, autenticação/autorização e regras de domínio.
- **DynamoDB** armazena dados de usuários, estabelecimentos, cardápios, produtos e happy hour.
- **S3** armazena imagens de produtos.
- **Scheduler/Worker** executa transições automáticas de estado do happy hour.

## 4. Estilo Arquitetural
Arquitetura baseada em **Hexagonal + Clean Architecture**:

- **Domínio (núcleo):** entidades, value objects, regras de negócio e contratos (ports).
- **Aplicação (casos de uso):** orquestra regras e transações de negócio.
- **Adapters de entrada:** controllers REST, DTOs, validações de entrada, handlers HTTP.
- **Adapters de saída:** repositórios DynamoDB, cliente S3, provedor de autenticação, publisher de eventos.
- **Infraestrutura:** configuração Spring, clients AWS, observabilidade, segurança, agendadores.

Regra de dependência: camadas externas dependem das internas; o domínio não conhece framework.

## 5. Estrutura lógica sugerida do backend
```text
br.com.cardapio
  ├─ domain
  │   ├─ model
  │   ├─ service
  │   └─ port
  │       ├─ in
  │       └─ out
  ├─ application
  │   ├─ usecase
  │   └─ dto
  ├─ adapters
  │   ├─ in
  │   │   └─ web (controllers, request/response, mappers)
  │   └─ out
  │       ├─ dynamodb
  │       ├─ s3
  │       └─ auth
  └─ infrastructure
      ├─ config
      ├─ security
      ├─ scheduler
      └─ observability
```

## 6. Modelo de dados e persistência (DynamoDB)
Tabelas (conforme documentação existente):

1. `usuarios` (PK: `usuario_id`) + GSI `por_estabelecimento` (`estabelecimento_id`, `usuario_id`)
2. `estabelecimentos` (PK: `estabelecimento_id`)
3. `cardapios` (PK: `estabelecimento_id`) — 1 cardápio por estabelecimento
4. `produtos` (PK: `estabelecimento_id`, SK: `produto_id`)
5. `happy_hours` (PK: `estabelecimento_id`) — no máximo 1 configuração ativa/agendada por estabelecimento
6. `precos_happy_hour` (PK: `versao_id`, SK: `produto_id`)

Diretrizes:
- Preço em centavos (`*_centavos`) para evitar erro de ponto flutuante.
- Horários em Unix UTC (`inicio_utc`, `fim_utc`).
- Autorização de `ADMIN_CARDAPIO` sempre usando `estabelecimento_id` do usuário autenticado, nunca do payload.

## 7. Fluxos críticos de negócio

### 7.1 Cadastro inicial de novo estabelecimento
`TransactWriteItems` atômico para criar:
1. estabelecimento;
2. cardápio vazio;
3. primeiro usuário `ADMIN_CARDAPIO`.

Com `attribute_not_exists` para evitar sobrescrita.

### 7.2 Cadastro de administrador em estabelecimento existente
- Validar existência de `estabelecimento_id` e cardápio.
- Criar novo `ADMIN_CARDAPIO` vinculado ao mesmo estabelecimento.

### 7.3 Happy Hour versionado
- Criar nova `versao_id`.
- Gravar preços promocionais em `precos_happy_hour` por produto participante.
- Publicar configuração em `happy_hours` com `status`, `inicio_utc`, `fim_utc`.
- Scheduler executa transições seguras:
  - `AGENDADO -> ATIVO`
  - `AGENDADO/ATIVO -> ENCERRADO`
- Leitura pública aplica promoção somente com:
  - `status == ATIVO`
  - `inicio_utc <= agora < fim_utc`

## 8. API REST (macro)
Recursos principais:
- `/auth`
- `/admin/plataforma/usuarios`
- `/admin/plataforma/estabelecimentos/{estabelecimentoId}/administradores`
- `/admin/cardapio/produtos`
- `/admin/cardapio/happy-hour`
- `/publico/cardapios/{slug-ou-id}`

Boas práticas:
- Versionamento (`/api/v1`).
- Erros padronizados (Problem Details / RFC 9457).
- Idempotência para operações sensíveis (header de idempotency-key).

## 9. Segurança
- OAuth2 Resource Server com JWT (ou Cognito/IdP equivalente).
- RBAC por perfil e políticas por estabelecimento.
- Senhas com hash forte (Argon2id ou BCrypt com custo adequado).
- TLS obrigatório.
- Auditoria de operações administrativas sensíveis.

## 10. Armazenamento de imagens (S3)
- Upload via backend ou URL pré-assinada.
- Validar MIME type, tamanho e extensão.
- Chaves por tenant: `estabelecimento_id/produtos/produto_id/{arquivo}`.
- Opcional recomendado: CloudFront para entrega e cache de imagens.

## 11. Frontend React
- App administrativo (plataforma e cardápio) consumindo API autenticada.
- Página pública de cardápio consumindo endpoint público.
- Estratégia de atualização no fim do HH para evitar exibição de preço promocional expirado.

## 12. Observabilidade e operação
- Logs estruturados (JSON) com `traceId`/`correlationId`.
- Métricas: latência por endpoint, taxa de erro, throughput, falhas em transações, eventos de HH.
- Tracing distribuído (OpenTelemetry).
- Alarmes para falhas de scheduler, aumento de `5xx` e throttling AWS.

## 13. Qualidade, Clean Code e governança
- Casos de uso pequenos e orientados a regra de negócio.
- Entidades sem dependência de framework.
- Mapeadores explícitos entre DTO e domínio.
- Testes por pirâmide:
  - unitários (domínio/casos de uso),
  - integração (adapters DynamoDB/S3),
  - contrato (API).
- Revisão de código com checklist de arquitetura (dependências, isolamento tenant, segurança).

## 14. Melhorias recomendadas
1. Adotar **Outbox Pattern** para integração confiável entre gravações DynamoDB e publicação de eventos de scheduler.
2. Definir **estratégia anti-duplicidade** de estabelecimento (normalização de CNPJ + índice de busca dedicado).
3. Implementar **feature flags** para liberar HH versionado por lote de clientes.
4. Criar **testes de resiliência temporal** para virada de HH (atraso, repetição e concorrência de eventos).
5. Planejar **cache público com TTL curto** e invalidação defensiva no término da janela HH.

## 15. Riscos e mitigação
- **Risco:** inconsistência entre estado HH e eventos externos.  
  **Mitigação:** transições condicionais + reconciliação periódica.
- **Risco:** acesso cruzado entre tenants por erro de implementação.  
  **Mitigação:** policy centralizada por `estabelecimento_id` e testes de autorização.
- **Risco:** custos por padrões de acesso não previstos no DynamoDB.  
  **Mitigação:** revisão de access patterns, métricas de consumo e ajustes de índices.

## 16. Decisões arquiteturais registradas
- Backend principal em **Java 25 + Spring Boot**.
- Arquitetura **Hexagonal** com princípios **Clean Architecture**.
- Persistência principal em **DynamoDB** com 6 tabelas e GSI para usuários por estabelecimento.
- Imagens de produtos em **AWS S3**.
- Frontend em **React**.
- API **RESTful** como interface principal da plataforma.
