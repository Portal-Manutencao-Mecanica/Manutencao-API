# Contrato da API para o frontend

Este arquivo e a referencia de implementacao do frontend para a API atual. Ele foi produzido a partir dos controllers, DTOs, regras de seguranca e tratamento de erros do projeto em 28/07/2026.

> Base local: `http://localhost:8080/api`  
> OpenAPI em desenvolvimento: `GET /api/v3/api-docs`  
> Swagger em desenvolvimento: `/api/swagger-ui.html`

## Convencoes globais

- Todos os IDs sao UUIDs em texto.
- Datas e horarios usam ISO 8601. Envie `LocalDateTime` como `YYYY-MM-DDTHH:mm:ss`, por exemplo `2026-07-28T14:30:00`.
- Exceto pelas rotas marcadas como publicas, envie `Authorization: Bearer <accessToken>`.
- As roles reais sao `ADMIN`, `COORDENADOR`, `PROFESSOR` e `ALUNO`.
- A API rejeita campos desconhecidos no JSON. Envie somente os campos documentados.
- `POST` de criacao responde `201`; exclusoes respondem `204` sem body; as demais operacoes bem-sucedidas respondem `200`, salvo onde indicado.
- Quando uma rota usa `Page<T>`, aceite `?page=0&size=20&sort=campo,asc`. O tamanho padrao e 20 e o maximo e 100.

```ts
type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; // pagina atual, iniciando em 0
  first: boolean;
  last: boolean;
  empty: boolean;
  sort: unknown;
};

type ApiError = {
  status: number;
  error: string;
  message: string;
  path: string;
  timestamp: string;
  errors: Record<string, string>;
};
```

No frontend, trate `401` como sessao invalida/expirada (renove pelo refresh ou redirecione ao login), `403` como falta de permissao, `404` como recurso ausente, `409` como conflito e `400` lendo `errors` por campo. O corpo de erro tem sempre o formato `ApiError` acima.

## Autenticacao e sessao

| Metodo | Rota | Acesso | Request | Resposta |
|---|---|---|---|---|
| POST | `/auth/login` | Publico | `LoginRequest` | `LoginResponse` |
| POST | `/auth/refresh` | Publico | `RefreshTokenRequest` | `LoginResponse` |
| POST | `/auth/logout` | Autenticado | `RefreshTokenRequest` | `204` |
| POST | `/auth/logout-all` | Autenticado | sem body | `204` |
| GET | `/auth/me` | Autenticado | - | `UserResponse` |

```ts
type LoginRequest = { email: string; password: string };
type RefreshTokenRequest = { refreshToken: string };
type LoginResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: string; // Bearer
  expiresIn: number; // segundos
  passwordChangeRequired: boolean;
  user: UserResponse;
};
type OrganizationSummary = { id: string; name: string };
type UserResponse = {
  id: string; name: string; username: string; email: string;
  role: Role; status: UserAccountStatus;
  passwordChangeRequired: boolean; organization: OrganizationSummary;
  numberCard: string; enabled: boolean; accountNonLocked: boolean;
  createdAt: string; updatedAt: string;
};
```

Guarde apenas o necessario para a sessao. O `refreshToken` deve ser enviado no body para renovar ou encerrar a sessao; o access token vai no cabecalho. Se `passwordChangeRequired` for `true`, a aplicacao deve direcionar para troca de senha: o backend permite somente `GET /users/me`, `PATCH /users/me/password` e logout nesse estado.

## Senha, perfil e preferencias

| Metodo | Rota | Acesso | Request | Resposta |
|---|---|---|---|---|
| POST | `/auth/password/forgot` | Publico | `{ email }` | `{ message }` |
| GET | `/auth/password/validate?token=...` | Publico | query `token` | `{ valid: boolean }` |
| POST | `/auth/password/reset` | Publico | `ResetPasswordRequest` | `{ message }` |
| GET | `/users/me` | Autenticado | - | `UserProfile` |
| PATCH | `/users/me` | Autenticado | `{ name }` | `UserProfile` |
| PATCH | `/users/me/password` | Autenticado | `ChangeOwnPasswordRequest` | `204` |
| PATCH | `/users/me/preferences` | Autenticado | `NotificationPreferencesPatch` | `UserProfile` |

```ts
type ResetPasswordRequest = {
  token: string; newPassword: string; passwordConfirmation: string;
};
type ChangeOwnPasswordRequest = {
  currentPassword: string; newPassword: string; passwordConfirmation: string;
};
type NotificationPreferencesPatch = {
  emailEnabled?: boolean; inAppEnabled?: boolean;
  occurrenceNotifications?: boolean; purchaseNotifications?: boolean;
  inspectionNotifications?: boolean;
};
type NotificationPreferences = Required<NotificationPreferencesPatch>;
type UserProfile = {
  id: string; name: string; username: string; email: string;
  role: Role; status: UserAccountStatus; passwordChangeRequired: boolean;
  organization: OrganizationSummary; preferences: NotificationPreferences;
  createdAt: string; updatedAt: string;
};
```

Nao existe rota publica de foto de perfil na API atual. Nao implemente upload de foto em `/users/me/photo`.

## Usuarios administrativos e importacao

| Metodo | Rota | Acesso | Request | Resposta |
|---|---|---|---|---|
| POST | `/users` | ADMIN, COORDENADOR | `CreateUserRequest` | `201 UserCreation` |
| POST | `/users/import` | ADMIN, COORDENADOR | `multipart/form-data`, parte `file` | `UserImportResponse` |
| PATCH | `/users/{id}/block` | ADMIN, COORDENADOR | `{ reason }` | `ManagedUser` |
| PATCH | `/users/{id}/unblock` | ADMIN, COORDENADOR | `{ reason }` | `ManagedUser` |
| PATCH | `/users/{id}/deactivate` | ADMIN, COORDENADOR | `{ reason }` | `ManagedUser` |
| PATCH | `/users/{id}/reactivate` | ADMIN, COORDENADOR | `{ reason }` | `ManagedUser` |
| PATCH | `/users/{id}/role` | ADMIN | `{ role: Role }` | `ManagedUser` |
| POST | `/users/{id}/resend-credentials` | ADMIN, COORDENADOR | sem body | `202 CredentialResend` |

```ts
type CreateUserRequest = {
  name: string; username: string; email: string; role: Role;
  organizationId?: string; // COORDENADOR opera na propria organizacao
};
type ManagedUser = {
  id: string; name: string; username: string; email: string; role: Role;
  status: UserAccountStatus; passwordChangeRequired: boolean;
  organization: OrganizationSummary; statusChangeReason: string | null;
  statusChangedAt: string | null; statusChangedBy: string | null; updatedAt: string;
};
type UserCreation = {
  id: string; name: string; username: string; email: string; role: Role;
  status: UserAccountStatus; passwordChangeRequired: boolean;
  organization: OrganizationSummary; credentialsSent: boolean;
  emailStatus: string; createdAt: string;
};
type CredentialResend = {
  userId: string; credentialsSent: boolean; emailStatus: string; message: string;
};
type UserImportItem = {
  id: string; row: number; name: string; username: string; email: string;
  role: Role; organization: string; status: 'CREATED' | 'FAILED';
  createdUserId: string | null; errorCode: string | null;
  field: string | null; message: string | null;
};
type UserImportResponse = {
  importId: string; filename: string; totalRows: number; created: number; failed: number;
  status: 'PROCESSING' | 'COMPLETED' | 'COMPLETED_WITH_ERRORS' | 'FAILED';
  createdAt: string; completedAt: string | null; items: UserImportItem[];
};
```

Para a importacao, envie um arquivo XLSX em `FormData` com a chave exata `file`; nao defina manualmente o header `Content-Type`, pois o navegador inclui o boundary. As colunas esperadas sao `name`, `username`, `email`, `role` e `organization`.

## Organizacoes

| Metodo | Rota | Acesso | Request | Resposta |
|---|---|---|---|---|
| POST | `/organizations` | ADMIN | `OrganizationCreate` | `201 Organization` |
| GET | `/organizations` | ADMIN, COORDENADOR | paginada | `Page<Organization>` |
| GET | `/organizations/{id}` | ADMIN, COORDENADOR | - | `Organization` |
| PATCH | `/organizations/{id}` | ADMIN | `OrganizationPatch` | `Organization` |
| PATCH | `/organizations/{id}/activate` | ADMIN | sem body | `Organization` |
| PATCH | `/organizations/{id}/deactivate` | ADMIN | sem body | `Organization` |

```ts
type OrganizationCreate = { name: string; type: OrganizationType; emailDomain: string };
type OrganizationPatch = { name?: string; type?: OrganizationType; emailDomain?: string };
type Organization = OrganizationSummary & {
  type: OrganizationType; emailDomain: string; active: boolean;
  createdAt: string; updatedAt: string;
};
```

## Cadastros de apoio (somente ADMIN)

| Recurso | Rotas | Body de criacao/PUT | Body de PATCH | Resposta |
|---|---|---|---|---|
| Equipamentos | `POST/GET /equipamento`; `GET/PUT/PATCH/DELETE /equipamento/{id}` | `{ name, sap?, unitPrice, availableQuantity }` | `{ name?, sap?, unitPrice?, availableQuantity? }` | `Equipment` (lista paginada) |
| Maquinas | `POST/GET /maquinas`; `GET/PUT/PATCH/DELETE /maquinas/{id}` | `{ name, patrimony, condition, tag?, placeId }` | `{ name?, patrimony?, condition?, tag? }` | `Machine` (lista paginada) |
| Locais | `POST/GET /lugar`; `GET/PUT/PATCH/DELETE /lugar/{id}` | `{ name }` | `{ name? }` | `Place[]` |
| Designacoes | `POST/GET /designacao`; `GET/PUT/PATCH/DELETE /designacao/{id}` | `{ sector }` | `{ sector? }` | `Designation` (lista paginada) |
| Materiais de apoio | `POST/GET /material-apoio`; `GET/PUT/PATCH/DELETE /material-apoio/{id}` | `{ title, description?, url, type }` | `{ title?, description?, url?, type? }` | `HelperMaterial` (lista paginada) |
| Turmas | `POST/GET /turma`; `GET /turma/ativos`; `GET/PUT/PATCH/DELETE /turma/{id}`; `PATCH /turma/{id}/inativar` | `{ acronym, teacherIds?, studentIds? }` | `{ acronym? }` | `ClassGroup` (lista paginada) |

```ts
type Equipment = { id: string; name: string; sap: string | null; unitPrice: number; availableQuantity: number };
type Machine = {
  id: string; name: string; patrimony: string; condition: EquipmentCondition;
  tag: string | null; placeId: string; placeName: string; createdAt: string;
};
type Place = { id: string; name: string };
type Designation = { id: string; sector: Sector };
type HelperMaterial = { id: string; title: string; description: string | null; url: string; type: HelperMaterialType };
type ClassGroup = {
  id: string; acronym: string; enabled: boolean; teachers: Teacher[]; students: Student[];
};
```

## Pessoas de turma (somente ADMIN)

Estas rotas sao de leitura. O ID para detalhes e o UUID `id`, nunca `numberCard`.

| Metodo | Rota | Resposta |
|---|---|---|
| GET | `/alunos` | `Student[]` |
| GET | `/alunos/ativos` | `Student[]` |
| GET | `/alunos/{id}` | `Student` |
| GET | `/professores` | `Teacher[]` |
| GET | `/professores/ativos` | `Teacher[]` |
| GET | `/professores/{id}` | `Teacher` |
| GET | `/coordenador` e `/coordernador` | `Page<Coordinator>` |
| GET | `/coordenador/ativos` e `/coordernador/ativos` | `Page<Coordinator>` |
| GET | `/coordenador/{id}` e `/coordernador/{id}` | `Coordinator` |

```ts
type Student = {
  id: string; numberCard: string; name: string; email: string; role: Role;
  classGroupIds: string[]; enabled: boolean; accountNonLocked: boolean;
  createdAt: string; updatedAt: string;
};
type Teacher = Student;
type Coordinator = {
  id: string; numberCard: string; name: string; email: string; role: Role;
  enabled: boolean; accountNonLocked: boolean; createdAt: string; updatedAt: string;
};
```

Use `/coordenador` no codigo novo. `/coordernador` existe apenas como alias retrocompativel.

## Operacoes de manutencao (qualquer usuario autenticado)

### Manutencao autonoma

| Metodo | Rota | Request/Resposta |
|---|---|---|
| POST | `/manutencao-autonoma` | `AutonomousMaintenanceRequest` -> `201 AutonomousMaintenance` |
| POST | `/manutencao-autonoma/create-all` | `AutonomousMaintenanceRequest[]` -> `201 AutonomousMaintenance[]` |
| GET | `/manutencao-autonoma` | `Page<AutonomousMaintenance>` |
| GET | `/manutencao-autonoma/{id}` | `AutonomousMaintenance` |
| PUT | `/manutencao-autonoma/{id}` | `AutonomousMaintenanceRequest` -> `AutonomousMaintenance` |
| DELETE | `/manutencao-autonoma/{id}` | `204` |
| GET | `/manutencao-autonoma/situacao/{situacao}` | `Page<AutonomousMaintenance>`; `situacao` e `OPERANDO` ou `NAO_OPERANDO` |

```ts
type AutonomousMaintenanceRequest = {
  equipmentSituation: EquipmentSituation; inspectedAt: string; inspectedMachineId: string;
  equipmentCondition: EquipmentCondition; identifiedNonconformities: string;
  responsibleTeacherId: string; responsibleStudentId: string;
};
type AutonomousMaintenance = AutonomousMaintenanceRequest & {
  id: string; inspectedMachineName: string; responsibleTeacherName: string;
  responsibleStudentName: string;
};
```

### Compras

| Metodo | Rota | Request/Resposta |
|---|---|---|
| POST | `/compras` | `BuyRequest` -> `201 Buy` |
| GET | `/compras` | `Page<Buy>` |
| GET | `/compras/{id}` | `Buy` |
| PUT | `/compras/{id}` | `BuyRequest` -> `Buy` |
| PATCH | `/compras/{id}` | `{ purchaseJustification? }` -> `Buy` |
| DELETE | `/compras/{id}` | `204` |
| GET | `/compras/status/{status}` | `Page<Buy>`; use `BuyStatus` |

```ts
type BuyItemRequest = {
  equipmentId: string; quantity: number; technicalSpecification?: string;
  sap?: string; patrimony?: string; tag?: string; mechanicalSet?: string;
};
type BuyRequest = {
  purchaseJustification: string; classGroupId: string; notifiedTeacherId?: string;
  items: BuyItemRequest[]; mediaIds?: string[];
};
type BuyItem = BuyItemRequest & { id: string; equipmentName: string };
type Media = {
  id: string; description: string | null; mediaType: MediaType; image: string;
  originalName: string; contentType: string; fileSize: number; createdAt: string;
};
type Buy = {
  id: string; status: BuyStatus; createdById: string; createdByName: string;
  notifiedTeacherId: string | null; notifiedTeacherName: string | null;
  purchaseJustification: string; classGroupId: string; classGroupAcronym: string;
  createdAt: string; items: BuyItem[]; media: Media[];
};
```

Nao ha controller publico de media neste projeto. Consuma `media` nas respostas, mas nao presuma uma rota de upload para preencher `mediaIds` ate que esse contrato seja criado.

### Ocorrencias 5S

| Metodo | Rota | Request/Resposta |
|---|---|---|
| POST | `/5s` | `Inconvenience5SRequest` -> `201 Inconvenience5S` |
| GET | `/5s` | `Page<Inconvenience5S>` |
| GET | `/5s/{id}` | `Inconvenience5S` |
| PUT | `/5s/{id}` | `Inconvenience5SRequest` -> `Inconvenience5S` |
| PATCH | `/5s/{id}` | `{ inconvenience?, description?, registrationPeriod? }` -> `Inconvenience5S` |
| DELETE | `/5s/{id}` | `204` |

```ts
type Inconvenience5SRequest = {
  inconvenience: string; placeId: string; notifiedTeacherId: string; classGroupId: string;
  involvedStudentIds: string[]; description?: string; registrationPeriod: RegistrationPeriod;
};
type Inconvenience5S = Inconvenience5SRequest & {
  id: string; status: Inconvenience5SStatus; placeName: string;
  notifiedTeacherName: string; createdAt: string; classGroupAcronym: string;
};
```

### Diario da maquina

| Metodo | Rota | Request/Resposta |
|---|---|---|
| POST | `/maquina-log` | `MachineLogRequest` -> `201 MachineLog` |
| GET | `/maquina-log` | `Page<MachineLog>` |
| GET | `/maquina-log/{id}` | `MachineLog` |
| PUT | `/maquina-log/{id}` | `MachineLogRequest` -> `MachineLog` |
| PATCH | `/maquina-log/{id}` | `MachineLogPatch` -> `MachineLog` |
| DELETE | `/maquina-log/{id}` | `204` |

```ts
type MachineLogRequest = {
  title?: string; description?: string; executionReport?: string;
  taskSituation: TaskSituation; machineId: string; servicePerformed?: string;
  responsibleTeacherId?: string; teacherConcludedAt?: string; executionStartedAt?: string;
  executionEndedAt?: string; plannedAction?: string; taskCriticality: TaskCriticality;
  placeId?: string; maintenanceType?: MaintenanceType; classGroupId?: string;
  assignedStudentIds?: string[]; reportLink?: string;
};
type MachineLogPatch = Omit<MachineLogRequest, 'machineId' | 'responsibleTeacherId' | 'placeId' | 'classGroupId' | 'assignedStudentIds'>;
type MachineLog = MachineLogRequest & {
  id: string; machineName: string; registeredAt: string; responsibleTeacherName: string | null;
  placeName: string | null; classGroupAcronym: string | null;
};
```

### Solicitacoes de manutencao

> A grafia exposta pelo controller e exatamente `/solicitao-manutencao`. Nao use `/solicitacao-manutencao` sem alterar a API.

| Metodo | Rota | Request/Resposta |
|---|---|---|
| POST | `/solicitao-manutencao` | `MaintenanceRequestInput` -> `201 MaintenanceRequest` |
| GET | `/solicitao-manutencao` | `MaintenanceRequest[]` (nao paginada) |
| GET | `/solicitao-manutencao/{id}` | `MaintenanceRequest` |
| PUT | `/solicitao-manutencao/{id}` | `MaintenanceRequestInput` -> `MaintenanceRequest` |
| PATCH | `/solicitao-manutencao/{id}` | `{ sector?, priority?, description? }` -> `MaintenanceRequest` |
| DELETE | `/solicitao-manutencao/{id}` | `204` |

```ts
type MaintenanceRequestInput = {
  sector: Sector; priority: Priority; assignedStudentIds: string[]; placeId: string;
  description: string; notifiedTeacherId: string; machineId: string;
};
type MaintenanceRequest = MaintenanceRequestInput & {
  id: string; status: MaintenanceRequestStatus; placeName: string; createdAt: string;
  notifiedTeacherName: string; machineName: string;
};
```

## Calendario de eventos (qualquer usuario autenticado)

| Metodo | Rota | Request/Resposta |
|---|---|---|
| POST | `/eventos` | `CalendarCreateRequest` -> `201 CalendarEvent` |
| GET | `/eventos` | `Page<CalendarEvent>` |
| GET | `/eventos/calendario` | `CalendarItem[]`, ordenado por `scheduledFor` crescente |
| GET | `/eventos/{id}` | `CalendarEvent` |
| PATCH | `/eventos/{id}` | `CalendarUpdate` -> `CalendarEvent` |
| PUT | `/eventos/{id}` | `CalendarUpdate` -> `CalendarEvent` |
| DELETE | `/eventos/{id}` | `204` |

```ts
type CalendarCreateRequest = {
  scheduledAction: string; criticality: TaskCriticality; scheduledFor: string; requestedAt: string;
  studentId?: string; teacherId: string; equipmentId: string; machineId: string; placeId: string;
  maintenanceType: MaintenanceType; status?: TaskSituation;
};
type CalendarUpdate = Partial<CalendarCreateRequest>;
type CalendarEvent = {
  id: string; scheduledAction: string; criticality: TaskCriticality; createdAt: string;
  scheduledFor: string; requestedAt: string; studentId: string | null; studentName: string | null;
  teacherId: string; teacherName: string; equipmentId: string; equipmentName: string;
  machineId: string; machineName: string; placeId: string; placeName: string;
  maintenanceType: MaintenanceType; status: TaskSituation;
};
type CalendarItem = { day: string; hour: string; title: string };
```

Use `/eventos/calendario` para um calendario simples: `day` e `hour` sao separados. Para modal, edicao e filtros com todas as referencias, use a resposta completa de `/eventos` ou `/eventos/{id}`.

## Notificacoes (usuario autenticado)

As notificacoes sao geradas internamente: o frontend nao possui rota de criacao.

| Metodo | Rota | Resultado |
|---|---|---|
| GET | `/notification` | `Page<Notification>` somente do usuario autenticado |
| GET | `/notification/{id}` | `Notification` se pertencer ao usuario |
| PUT | `/notification/{id}` | marca como lida e retorna `Notification` |
| PATCH | `/notification/{id}/read` | alias para marcar como lida |
| PATCH | `/notification/{id}/toggle-read` | alterna leitura |
| PATCH | `/notification/read-all` | marca todas como lidas, `204` |
| GET | `/notification/unread-count` | numero simples, por exemplo `3` |
| DELETE | `/notification/{id}` | `204` |

```ts
type Notification = {
  id: string; email: string; title: string; about: string;
  description: string; statusRead: boolean;
};
```

## Enums aceitos

```ts
type Role = 'ADMIN' | 'ALUNO' | 'PROFESSOR' | 'COORDENADOR';
type UserAccountStatus =
  | 'PENDING_FIRST_ACCESS'
  | 'ACTIVE'
  | 'TEMPORARILY_LOCKED'
  | 'BLOCKED'
  | 'DISABLED'
  | 'PASSWORD_EXPIRED';
type OrganizationType = 'SENAI' | 'WEG' | 'OTHER';
type EquipmentCondition = 'CONFORME' | 'NAO_CONFORME';
type EquipmentSituation = 'OPERANDO' | 'NAO_OPERANDO';
type BuyStatus = 'ENTREGUE' | 'EM_ANALISE' | 'PEDIDO_EM_ANDAMENTO' | 'NAO_VISUALIZADO';
type HelperMaterialType = 'TECNICO' | 'LUBRIFICACAO' | 'MANUTENCAO_PREVENTIVA' | 'MANUAL';
type Inconvenience5SStatus = 'NAO_VISUALIZADA' | 'EM_ANDAMENTO' | 'NOTIFICADO';
type RegistrationPeriod = 'MATUTINO' | 'VESPERTINO' | 'NOTURNO';
type Sector = 'AREA_NAO_DESIGNADA' | 'CENTRO_WEG' | 'WEG_MANUTENCAO';
type Priority = 'ALTA' | 'MEDIA' | 'BAIXA';
type MaintenanceRequestStatus = 'NAO_VISUALIZADA' | 'FINALIZADA' | 'EM_ANALISE';
type TaskCriticality = 'BAIXA' | 'MEDIA' | 'ALTA';
type TaskSituation = 'PENDENTE' | 'EM_ANDAMENTO' | 'CONCLUIDA';
type MaintenanceType = 'PREVENTIVA' | 'CORRETIVA' | 'PREDITIVA' | 'AUTONOMA';
type MediaType = 'MAINTENANCE_REQUEST' | 'PURCHASE_REQUEST' | 'INCONVENIENCE_5S' |
  'EQUIPMENT' | 'MACHINE_LOG' | 'AUTONOMOUS_MAINTENANCE';
```

Os valores acima foram extraidos dos enums atuais do backend. Enumere-os no frontend para evitar enviar valores fora do contrato.

## Rotas tecnicas

| Metodo | Rota | Acesso | Uso |
|---|---|---|---|
| GET | `/actuator/health` | Publico | health check |
| GET | `/actuator/info` e `/actuator/metrics` | ADMIN | observabilidade |
| GET | `/v3/api-docs` | Publico fora de producao | contrato OpenAPI em JSON |
| GET | `/swagger-ui.html` | Publico fora de producao | exploracao manual |

`/admin` possui um controller protegido por `ADMIN`, mas nao declara nenhum metodo HTTP proprio; portanto nao ha tela ou chamada de frontend para ele atualmente.

## Exemplo de cliente HTTP

```ts
const API_URL = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

export async function api<T>(path: string, init: RequestInit = {}, token?: string): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, {
    ...init,
    headers: {
      Accept: 'application/json',
      ...(init.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...init.headers,
    },
  });

  if (!response.ok) {
    const error = (await response.json()) as ApiError;
    throw error;
  }
  if (response.status === 204) return undefined as T;
  return response.json() as Promise<T>;
}
```

Ao usar `FormData` em `/users/import`, passe o `FormData` como `body` e nao sobrescreva o `Content-Type`.
## Teacher approval for maintenance requests

A maintenance request can only be created by an authenticated `ALUNO`. The logged-in student becomes its sole requester and the initial status is `PENDENTE_APROVACAO_PROFESSOR`.

| Method | Endpoint | Access | Body |
|---|---|---|---|
| PATCH | `/solicitao-manutencao/{id}/aprovacao` | Only the notified `PROFESSOR` | `{ "approved": boolean, "reason"?: string }` |

The decision endpoint returns the regular `MaintenanceRequest` plus:

```ts
approvedById: string | null;
approvedByName: string | null;
approvedAt: string | null;
rejectionReason: string | null;
```

Possible approval statuses: `PENDENTE_APROVACAO_PROFESSOR`, `APROVADA_PELO_PROFESSOR`, and `REPROVADA_PELO_PROFESSOR`.

## Automatic notifications

The backend creates in-app notifications and, when `emailEnabled` is true, schedules an asynchronous email after the transaction commits. Email delivery failures are logged and never roll back the main operation.

Initial events: a maintenance request goes to its notified teacher; an approval/rejection goes to the requesting student; creation/update of a machine log goes to its responsible teacher and assigned students. SMTP variables remain `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`, `MAIL_SMTP_AUTH`, and `MAIL_STARTTLS_ENABLED`.