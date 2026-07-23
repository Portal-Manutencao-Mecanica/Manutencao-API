# API de Manutenção — Rotas para integração

Todas as rotas abaixo usam JSON, salvo quando indicado. Nas rotas protegidas, enviar:

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

## Catálogo completo de endpoints

Esta é a lista integral das rotas expostas pelos controllers atuais:

| Método | Endpoint | Informação principal |
|---|---|---|
| `POST` | `/auth/login` | `email`, `password` |
| `GET` | `/auth/me` | Usuário autenticado |
| `POST` | `/excel/import` | Multipart `file` |
| `POST` | `/equipamento` | Dados do equipamento |
| `GET` | `/equipamento` | Lista equipamentos |
| `GET` | `/equipamento/{id}` | `id` do equipamento |
| `PUT` | `/equipamento/{id}` | Dados completos do equipamento |
| `PATCH` | `/equipamento/{id}` | Campos parciais do equipamento |
| `DELETE` | `/equipamento/{id}` | `id` do equipamento |
| `POST` | `/lugar` | `name` |
| `GET` | `/lugar` | Lista lugares |
| `GET` | `/lugar/{id}` | `id` do lugar |
| `PUT` | `/lugar/{id}` | `name` |
| `PATCH` | `/lugar/{id}` | `name` opcional |
| `DELETE` | `/lugar/{id}` | `id` do lugar |
| `POST` | `/maquinas` | Dados da máquina |
| `GET` | `/maquinas` | Lista máquinas |
| `GET` | `/maquinas/{id}` | `id` da máquina |
| `PUT` | `/maquinas/{id}` | Dados completos da máquina |
| `PATCH` | `/maquinas/{id}` | Campos parciais da máquina |
| `DELETE` | `/maquinas/{id}` | `id` da máquina |
| `POST` | `/solicitao-manutencao` | Dados da solicitação |
| `GET` | `/solicitao-manutencao` | Lista solicitações |
| `GET` | `/solicitao-manutencao/{id}` | `id` da solicitação |
| `PUT` | `/solicitao-manutencao/{id}` | Dados completos da solicitação |
| `PATCH` | `/solicitao-manutencao/{id}` | `sector`, `priority`, `description` |
| `DELETE` | `/solicitao-manutencao/{id}` | `id` da solicitação |
| `POST` | `/eventos` | Dados do evento |
| `GET` | `/eventos` | Lista eventos |
| `GET` | `/eventos/{id}` | `id` do evento |
| `PUT` | `/eventos/{id}` | Dados do evento |
| `DELETE` | `/eventos/{id}` | `id` do evento |
| `POST` | `/designacao` | `sector` |
| `GET` | `/designacao` | Lista designações |
| `GET` | `/designacao/{id}` | `id` da designação |
| `PUT` | `/designacao/{id}` | `sector` |
| `PATCH` | `/designacao/{id}` | `sector` opcional |
| `DELETE` | `/designacao/{id}` | `id` da designação |
| `POST` | `/material-apoio` | Material de apoio |
| `GET` | `/material-apoio` | Lista materiais |
| `GET` | `/material-apoio/{id}` | `id` do material |
| `PUT` | `/material-apoio/{id}` | Dados completos do material |
| `PATCH` | `/material-apoio/{id}` | Campos parciais do material |
| `DELETE` | `/material-apoio/{id}` | `id` do material |
| `POST` | `/alunos` | Dados do aluno |
| `GET` | `/alunos` | Lista alunos |
| `GET` | `/alunos/ativos` | Lista alunos ativos |
| `GET` | `/alunos/{id}` | `id` do aluno |
| `PUT` | `/alunos/{id}` | Dados completos do aluno |
| `PATCH` | `/alunos/{id}` | `name`, `email`, `password` |
| `PATCH` | `/alunos/{id}/inativar` | Inativa o aluno |
| `DELETE` | `/alunos/{id}` | `id` do aluno |
| `POST` | `/professores` | Dados do professor |
| `GET` | `/professores` | Lista professores |
| `GET` | `/professores/ativos` | Lista professores ativos |
| `GET` | `/professores/{id}` | `id` do professor |
| `PUT` | `/professores/{id}` | Dados completos do professor |
| `PATCH` | `/professores/{id}` | `name`, `email`, `password` |
| `PATCH` | `/professores/{id}/inativar` | Inativa o professor |
| `DELETE` | `/professores/{id}` | `id` do professor |
| `POST` | `/coordenador` ou `/coordernador` | Dados do coordenador |
| `GET` | `/coordenador` ou `/coordernador` | Lista coordenadores |
| `GET` | `/coordenador/ativos` ou `/coordernador/ativos` | Lista coordenadores ativos |
| `GET` | `/coordenador/{id}` ou `/coordernador/{id}` | `id` do coordenador |
| `PUT` | `/coordenador/{id}` ou `/coordernador/{id}` | Dados completos do coordenador |
| `PATCH` | `/coordenador/{id}` ou `/coordernador/{id}` | `name`, `email`, `password` |
| `PATCH` | `/coordenador/{id}/inativar` ou `/coordernador/{id}/inativar` | Inativa o coordenador |
| `DELETE` | `/coordenador/{id}` ou `/coordernador/{id}` | `id` do coordenador |
| `POST` | `/turma` | `acronym`, `teacherIds`, `studentIds` |
| `GET` | `/turma` | Lista turmas |
| `GET` | `/turma/ativos` | Lista turmas ativas |
| `GET` | `/turma/{id}` | `id` da turma |
| `PUT` | `/turma/{id}` | Dados da turma |
| `PATCH` | `/turma/{id}` | `acronym` |
| `PATCH` | `/turma/{id}/inativar` | Inativa a turma |
| `DELETE` | `/turma/{id}` | `id` da turma |
| `POST` | `/maquina-log` | Dados do log |
| `GET` | `/maquina-log` | Lista logs |
| `GET` | `/maquina-log/{id}` | `id` do log |
| `PUT` | `/maquina-log/{id}` | Dados completos do log |
| `PATCH` | `/maquina-log/{id}` | Campos parciais do log |
| `DELETE` | `/maquina-log/{id}` | `id` do log |
| `POST` | `/5s` | Dados da ocorrência 5S |
| `GET` | `/5s` | Lista ocorrências 5S |
| `GET` | `/5s/{id}` | `id` da ocorrência |
| `PUT` | `/5s/{id}` | Dados completos da ocorrência |
| `PATCH` | `/5s/{id}` | Campos parciais da ocorrência |
| `DELETE` | `/5s/{id}` | `id` da ocorrência |
| `POST` | `/compras` | Dados da compra |
| `GET` | `/compras` | Lista compras |
| `GET` | `/compras/{id}` | `id` da compra |
| `PUT` | `/compras/{id}` | Dados completos da compra |
| `PATCH` | `/compras/{id}` | `purchaseJustification` |
| `DELETE` | `/compras/{id}` | `id` da compra |
| `GET` | `/compras/status/{status}` | Status da compra |
| `POST` | `/manutencao-autonoma` | Dados da manutenção |
| `POST` | `/manutencao-autonoma/create-all` | Lista de manutenções |
| `GET` | `/manutencao-autonoma` | Lista manutenções |
| `GET` | `/manutencao-autonoma/{id}` | `id` da manutenção |
| `PUT` | `/manutencao-autonoma/{id}` | Dados da manutenção |
| `DELETE` | `/manutencao-autonoma/{id}` | `id` da manutenção |
| `GET` | `/manutencao-autonoma/situacao/{situacao}` | Situação do equipamento |
| `POST` | `/notification` | Dados da notificação |
| `GET` | `/notification` | Lista notificações |
| `GET` | `/notification/{id}` | `id` da notificação |
| `PUT` | `/notification/{id}` | Marca como lida (sem body) |
| `PATCH` | `/notification/{id}/read` | Marca como lida |
| `PATCH` | `/notification/{id}/toggle-read` | Alterna entre lida e não lida |
| `PATCH` | `/notification/read-all` | Marca todas como lidas |
| `DELETE` | `/notification/{id}` | `id` da notificação |

O controller `/admin` existe, mas atualmente não possui endpoints implementados.

## Autenticação

### `POST /auth/login`

Body obrigatório:

```json
{
  "email": "usuario@exemplo.com",
  "password": "senha"
}
```

Retorna `accessToken`, `tokenType`, `expiresIn` e `user`.

### `GET /auth/me`

Retorna o usuário autenticado: `id`, `numberCard`, `name`, `email`, `role`, `enabled`, `accountNonLocked`, `createdAt` e `updatedAt`.

## Rotas CRUD

Para os recursos abaixo, estão disponíveis as rotas:

| Recurso | Base |
|---|---|
| Equipamentos | `/equipamento` |
| Lugares | `/lugar` |
| Máquinas | `/maquinas` |
| Solicitações de manutenção | `/solicitao-manutencao` |
| Eventos | `/eventos` |
| Designações | `/designacao` |
| Materiais de apoio | `/material-apoio` |
| Alunos | `/alunos` |
| Professores | `/professores` |
| Coordenadores | `/coordenador` ou `/coordernador` |
| Turmas | `/turma` |
| Logs de máquina | `/maquina-log` |
| Ocorrências 5S | `/5s` |
| Compras | `/compras` |
| Manutenções autônomas | `/manutencao-autonoma` |
| Notificações | `/notification` |

### Rotas comuns

Para cada base acima, exceto onde indicado de forma diferente:

- `GET /base`: lista todos os registros.
- `GET /base/{id}`: busca um registro pelo `id` (path param obrigatório).
- `POST /base`: cria um registro com o body indicado abaixo; retorna `201`.
- `PUT /base/{id}`: substitui/atualiza o registro; recebe o mesmo body de criação, salvo indicação abaixo.
- `PATCH /base/{id}`: atualização parcial; recebe somente os campos desejados.
- `DELETE /base/{id}`: exclui o registro; retorna `204`.

### Equipamento — `/equipamento`

`POST`/`PUT` body:

```json
{
  "name": "Torno mecânico",
  "sap": "SAP-001",
  "unitPrice": 1500.00,
  "availableQuantity": 2
}
```

`PATCH` aceita: `name`, `sap`, `unitPrice`, `availableQuantity`.

Retorno: `id`, `numberCard`, `name`, `sap`, `unitPrice`, `availableQuantity`.

### Lugar — `/lugar`

`POST`/`PUT` body: `{ "name": "Laboratório" }`.

`PATCH` aceita: `name`.

Retorno: `id`, `numberCard`, `name`.

### Máquina — `/maquinas`

`POST`/`PUT` body:

```json
{
  "name": "Prensa hidráulica",
  "patrimony": "PAT-001",
  "condition": "CONFORME",
  "tag": "PR-01",
  "placeId": 1
}
```

`PATCH` aceita: `name`, `patrimony`, `condition`, `tag`. `placeId` não é alterado pelo PATCH.

### Solicitação de manutenção — `/solicitao-manutencao`

`POST`/`PUT` body:

```json
{
  "sector": "CENTRO_WEG",
  "priority": "ALTA",
  "assignedStudentIds": [1, 2],
  "placeId": 1,
  "description": "Descrição do problema",
  "notifiedTeacherId": 1,
  "machineId": 1
}
```

`PATCH` aceita: `sector`, `priority`, `description`.

Retorno: `id`, `numberCard`, `status`, `sector`, `priority`, `assignedStudentIds`, `placeId`, `placeName`, `description`, `createdAt`, `notifiedTeacherId`, `notifiedTeacherName`, `machineId` e `machineName`.

### Evento — `/eventos`

`POST` body:

```json
{
  "scheduledAction": "Inspecionar equipamento",
  "criticality": "MEDIA",
  "scheduledFor": "2026-07-30T10:00:00",
  "requestedAt": "2026-07-23T10:00:00",
  "studentId": 1,
  "teacherId": 1,
  "equipmentId": 1,
  "machineId": 1,
  "placeId": 1,
  "maintenanceType": "PREVENTIVA",
  "status": "PENDENTE"
}
```

`PUT` aceita os mesmos campos, todos opcionais. Não há `PATCH`.

Retorno: `id`, `numberCard`, `scheduledAction`, `criticality`, `scheduledFor`, `requestedAt`, `studentId`, `studentName`, `teacherId`, `teacherName`, `equipmentId`, `equipmentName`, `machineId`, `machineName`, `placeId`, `placeName`, `maintenanceType` e `status`.

### Designação — `/designacao`

`POST`/`PUT` body: `{ "sector": "CENTRO_WEG" }`.

`PATCH` aceita: `sector`.

Retorno: `id`, `numberCard` e `sector`.

### Material de apoio — `/material-apoio`

`POST`/`PUT` body:

```json
{
  "title": "Manual do equipamento",
  "description": "Descrição do material",
  "url": "https://exemplo.com/manual.pdf",
  "type": "TECNICO"
}
```

`PATCH` aceita: `title`, `description`, `url`, `type`.

### Aluno — `/alunos`

`POST`/`PUT` body:

```json
{
  "name": "Nome do aluno",
  "email": "aluno@exemplo.com",
  "password": "Senha@123",
  "classGroupIds": [1]
}
```

`PATCH` aceita: `name`, `email`, `password`.

Rotas adicionais:

- `GET /alunos/ativos`: lista somente alunos com `enabled: true`.
- `PATCH /alunos/{id}/inativar`: altera `enabled` para `false`.

### Professor — `/professores`

`POST`/`PUT` body:

```json
{
  "name": "Nome do professor",
  "email": "professor@exemplo.com",
  "password": "Senha@123",
  "classGroupIds": [1]
}
```

`PATCH` aceita: `name`, `email`, `password`.

Rotas adicionais:

- `GET /professores/ativos`: lista somente professores com `enabled: true`.
- `PATCH /professores/{id}/inativar`: altera `enabled` para `false`.

### Coordenador — `/coordenador` ou `/coordernador`

`POST`/`PUT` body:

```json
{
  "name": "Nome do coordenador",
  "email": "coordenador@exemplo.com",
  "password": "Senha@123"
}
```

`PATCH` aceita: `name`, `email`, `password`.

Rotas adicionais:

- `GET /coordenador/ativos`: lista somente coordenadores com `enabled: true`.
- `PATCH /coordenador/{id}/inativar`: altera `enabled` para `false`.

### Turma — `/turma`

`POST`/`PUT` body:

```json
{
  "acronym": "MEC-01",
  "teacherIds": [1, 2],
  "studentIds": [1, 2]
}
```

`PATCH` aceita: `acronym`.

Rotas adicionais:

- `GET /turma/ativos`: lista somente turmas com `enabled: true`.
- `PATCH /turma/{id}/inativar`: altera `enabled` para `false`.

### Log de máquina — `/maquina-log`

`POST`/`PUT` body:

```json
{
  "title": "Troca de correia",
  "description": "Descrição",
  "executionReport": "Relatório da execução",
  "taskSituation": "PENDENTE",
  "machineId": 1,
  "servicePerformed": "Troca realizada",
  "responsibleTeacherId": 1,
  "teacherConcludedAt": "2026-07-23T12:00:00",
  "executionStartedAt": "2026-07-23T10:00:00",
  "executionEndedAt": "2026-07-23T12:00:00",
  "plannedAction": "Verificar desgaste",
  "taskCriticality": "MEDIA",
  "placeId": 1,
  "maintenanceType": "PREVENTIVA",
  "classGroupId": 1,
  "assignedStudentIds": [1],
  "reportLink": "https://exemplo.com/relatorio"
}
```

`PATCH` aceita: `title`, `description`, `executionReport`, `taskSituation`, `servicePerformed`, `teacherConcludedAt`, `executionStartedAt`, `executionEndedAt`, `plannedAction`, `taskCriticality`, `maintenanceType` e `reportLink`.

### Ocorrência 5S — `/5s`

`POST`/`PUT` body:

```json
{
  "inconvenience": "Vazamento identificado",
  "placeId": 1,
  "notifiedTeacherId": 1,
  "classGroupId": 1,
  "involvedStudentIds": [1, 2],
  "description": "Descrição",
  "registrationPeriod": "MATUTINO"
}
```

`PATCH` aceita: `inconvenience`, `description`, `registrationPeriod`.

### Compra — `/compras`

`POST`/`PUT` body:

```json
{
  "purchaseJustification": "Reposição de equipamento",
  "classGroupId": 1,
  "notifiedTeacherId": 1,
  "items": [
    {
      "equipmentId": 1,
      "quantity": 2,
      "technicalSpecification": "Especificação",
      "sap": "SAP-001",
      "patrimony": "PAT-001",
      "tag": "TAG-001",
      "mechanicalSet": "Conjunto mecânico"
    }
  ],
  "mediaIds": [1]
}
```

`PATCH` aceita: `purchaseJustification`.

Rotas adicionais:

- `GET /compras/status/{status}`: lista compras por status.
- Status aceitos: `ENTREGUE`, `EM_ANALISE`, `PEDIDO_EM_ANDAMENTO`.

### Manutenção autônoma — `/manutencao-autonoma`

`POST`/`PUT` body:

```json
{
  "equipmentSituation": "OPERANDO",
  "inspectedAt": "2026-07-23T10:00:00",
  "inspectedMachineId": 1,
  "equipmentCondition": "CONFORME",
  "identifiedNonconformities": "Nenhuma",
  "responsibleTeacherId": 1,
  "responsibleStudentId": 1
}
```

Rotas adicionais:

- `POST /manutencao-autonoma/create-all`: recebe uma lista dos objetos acima.
- `GET /manutencao-autonoma/situacao/{situacao}`: lista por situação do equipamento.
- Situação aceita: `OPERANDO`.

### Notificação — `/notification`

`POST /notification` body:

```json
{
  "email": "destinatario@exemplo.com",
  "title": "Nova solicitação",
  "about": "Manutenção",
  "description": "Descrição da notificação"
}
```

`PUT /notification/{id}` e `PATCH /notification/{id}/read` marcam uma notificação como lida; não possuem body.

`PATCH /notification/{id}/toggle-read` alterna o campo `statusRead` entre `true` e `false`; não possui body.

`PATCH /notification/read-all` marca todas as notificações como lidas; não possui body.

`DELETE /notification/{id}` remove a notificação.

Retorno: `id`, `numberCard`, `email`, `title`, `about`, `description` e `statusRead`.

## Importação

### `POST /excel/import`

Enviar como `multipart/form-data`, com o campo obrigatório `file` contendo a planilha de usuários.

Retorna uma mensagem de confirmação.

## Enumeradores aceitos

- `condition`: `CONFORME`
- `equipmentSituation`: `OPERANDO`
- `type` de material: `TECNICO`, `LUBRIFICACAO`, `MANUTENCAO_PREVENTIVA`
- `maintenanceType`: `PREVENTIVA`, `CORRETIVA`, `PREDITIVA`
- `criticality`/`taskCriticality`: `BAIXA`, `MEDIA`
- `priority`: `ALTA`, `MEDIA`
- `registrationPeriod`: `MATUTINO`, `VESPERTINO`
- `sector`: `AREA_NAO_DESIGNADA`, `CENTRO_WEG`
- `taskSituation`: `PENDENTE`, `EM_ANDAMENTO`
- `status` de compra: `ENTREGUE`, `EM_ANALISE`, `PEDIDO_EM_ANDAMENTO`
- `status` de evento: `PENDENTE`, `EM_ANDAMENTO`

## Formato de datas

Enviar datas no formato ISO-8601, por exemplo: `2026-07-23T10:00:00`.

## Respostas sem conteúdo

As operações `DELETE` retornam HTTP `204 No Content`. Erros de validação e requisições inválidas retornam HTTP `400`; recurso inexistente retorna HTTP `404`; falha no envio de e-mail retorna HTTP `502`; autenticação inválida retorna HTTP `401`.
