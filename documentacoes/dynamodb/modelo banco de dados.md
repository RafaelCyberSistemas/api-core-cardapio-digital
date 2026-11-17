# DynamoDB para a plataforma de cardápios digitais

O modelo usa seis tabelas no DynamoDB. Cada estabelecimento tem um cardápio e, na primeira versão, no máximo um happy hour configurado. O cardápio nasce sem produtos quando o administrador da plataforma cadastra o primeiro administrador de cardápio daquele estabelecimento. Os próximos administradores são vinculados pelo ID interno do estabelecimento já cadastrado.

## Tabelas e chaves

| Tabela | Chave de partição (PK) | Chave de ordenação (SK) | Dados relevantes |
| --- | --- | --- | --- |
| `usuarios` | `usuario_id` | — | `tipo`: `ADMIN_PLATAFORMA` ou `ADMIN_CARDAPIO`. Apenas `ADMIN_CARDAPIO` tem `estabelecimento_id`, um único valor. |
| `estabelecimentos` | `estabelecimento_id` | — | Dados do estabelecimento informados no primeiro cadastro. Os campos cadastrais não são impostos por este modelo. |
| `cardapios` | `estabelecimento_id` | — | Dados do cardápio. A própria chave impede dois cardápios para o mesmo estabelecimento. A ausência de itens em `produtos` representa o cardápio vazio. |
| `produtos` | `estabelecimento_id` | `produto_id` | Dados normais do produto, como nome, descrição e `preco_normal_centavos`. |
| `happy_hours` | `estabelecimento_id` | — | `versao_id`, `inicio_utc`, `fim_utc` e `status`. A própria chave limita o estabelecimento a um item de happy hour. |
| `precos_happy_hour` | `versao_id` | `produto_id` | `preco_promocional_centavos` para cada produto participante. `versao_id` é globalmente único e é gerado no backend. |

As chaves são strings. Preços são números inteiros na menor unidade monetária; no exemplo, centavos. Os horários são números no formato Unix em UTC, com a mesma unidade em toda a aplicação. No Terraform, os blocos `attribute` declaram somente as chaves das tabelas e dos índices; o backend valida os demais campos.

O índice `usuarios.por_estabelecimento` permite listar os administradores vinculados com `Query` por `estabelecimento_id`, sem varrer a tabela. Como a projeção é `KEYS_ONLY`, os dados adicionais de usuário vêm da tabela principal. O índice é eventualmente consistente: use a tabela principal para verificar permissões, não o índice.

## Cadastro e controle de acesso

1. **Novo estabelecimento:** o backend confirma que o solicitante é `ADMIN_PLATAFORMA`. Em uma única operação `TransactWriteItems`, grava o estabelecimento, o cardápio vazio e o primeiro `ADMIN_CARDAPIO` já vinculado, usando `attribute_not_exists` sobre a chave de cada item. Se uma gravação falhar, nenhuma das três é aplicada. A operação de criar um novo estabelecimento é distinta da de selecionar um existente; o modelo não tenta identificar duplicatas pelo nome.
2. **Estabelecimento existente:** o administrador da plataforma seleciona seu ID interno. A transação confirma a existência do estabelecimento e do cardápio e grava apenas o novo usuário com `estabelecimento_id`, sem recriar o cardápio ou sobrescrever um usuário existente. Para criar outro `ADMIN_PLATAFORMA`, grava-se apenas o usuário, sem vínculo com estabelecimento.
3. **Produtos e happy hour:** o backend autoriza apenas `ADMIN_CARDAPIO` a gerenciar os itens do seu próprio estabelecimento. Ele obtém `estabelecimento_id` do registro do usuário autenticado, não do corpo da requisição. Ao cadastrar preços promocionais, valida que cada produto pertence a esse estabelecimento.

## Ativação e encerramento do happy hour

O preço normal nunca é sobrescrito. O administrador informa início, fim e o preço promocional de cada produto participante. O backend valida `inicio_utc < fim_utc` e grava os preços de uma nova versão antes de publicar a configuração que os referencia.

O agendador executa duas transições: `AGENDADO` → `ATIVO` no início e `AGENDADO` ou `ATIVO` → `ENCERRADO` no fim. Cada evento leva `estabelecimento_id` e `versao_id`; a atualização usa condições sobre a versão, o estado atual e o horário. A ativação só vale dentro da janela e não pode reabrir uma versão encerrada. A desativação só vale a partir de `fim_utc`. As condições impedem que tarefas repetidas, atrasadas ou de uma configuração anterior alterem indevidamente o estado atual.

Na leitura pública, a promoção só se aplica quando `status == ATIVO` **e** `inicio_utc <= agora < fim_utc`. Se essas condições forem satisfeitas, a API consulta os preços da `versao_id` e aplica apenas os dos produtos participantes. Fora dessa condição, exibe os dados normais do produto. Assim, no instante de fim, novas leituras voltam aos preços normais mesmo se o agendador ainda não tiver atualizado o estado. Se a ativação atrasar, por outro lado, os preços promocionais só aparecem depois que o estado se tornar `ATIVO`.

Para o fluxo público, leia a configuração diretamente da tabela `happy_hours` com `ConsistentRead = true`. Se houver cache da resposta, ele não deve servir preços promocionais após o fim da janela; a opção mais simples é não armazenar a resposta com preços em cache. Em telas já abertas, o cliente também precisa atualizar a exibição no fim. TTL do DynamoDB não é usado para encerrar a promoção: ele exclui itens de forma assíncrona e não controla o horário de exibição.

## Código Terraform e aplicação

O arquivo [`dynamodb-cardapio.tf`](dynamodb-cardapio.tf) contém o código completo para provisionar as seis tabelas em modo sob demanda. O trecho abaixo ilustra as chaves usadas para garantir um cardápio e um happy hour por estabelecimento:

```hcl
cardapios = {
  pk = "estabelecimento_id"
  sk = null
}
happy_hours = {
  pk = "estabelecimento_id"
  sk = null
}
```

Em um diretório com o arquivo `.tf` e com credenciais AWS autorizadas, informe a região e o prefixo das tabelas:

```bash
export TF_VAR_aws_region="<regiao-aws>"
export TF_VAR_table_prefix="<prefixo-do-ambiente>"
terraform init
terraform plan -out=cardapio.tfplan
terraform apply cardapio.tfplan
```

O código provisiona somente as tabelas e o índice. As transações, a autenticação, a API e a configuração do agendador não são provisionadas por ele. A integração do agendador com o backend precisa tratar e reconciliar falhas ao registrar os eventos de início e fim, porque gravar no DynamoDB e registrar um evento externo não formam uma única transação.