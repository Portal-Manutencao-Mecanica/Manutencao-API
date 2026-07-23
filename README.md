# Portal de Manutenção — API

API REST do Portal de Manutenção, construída com Java 21, Spring Boot 4, Spring Security, JWT, Spring Data JPA, PostgreSQL e Flyway.

O estado atual corresponde à conclusão das Fases 1 e 2: identificadores UUID, organizações, autenticação stateless, ciclo completo de credenciais, importação XLSX, primeiro acesso, recuperação de senha, perfil, administração de usuários, auditoria e respostas de erro padronizadas.

## Requisitos para execução

- Java 21 ou superior;
- PostgreSQL 17 ou compatível;
- Docker, opcional;
- servidor SMTP para os fluxos que enviam e-mail.

Copie `.env.example` e configure os valores como variáveis de ambiente do processo. O Spring Boot não carrega arquivos `.env` automaticamente.

Variáveis obrigatórias:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD
JWT_SECRET
MAIL_HOST
MAIL_USERNAME
MAIL_PASSWORD
FRONTEND_URL
```

`JWT_SECRET` deve ser um valor Base64 que represente pelo menos 32 bytes aleatórios. Não armazene senhas, tokens ou segredos no repositório. No perfil `dev`, o seed de administrador só é habilitado quando `DEV_ADMIN_PASSWORD` é fornecida pelo ambiente.

Para iniciar:

```powershell
.\mvnw.cmd spring-boot:run
```

Para executar os testes:

```powershell
.\mvnw.cmd test
```

## URLs

O contexto padrão da aplicação é `/api`.

```text
API:        http://localhost:8080/api
Swagger:    http://localhost:8080/api/swagger-ui.html
OpenAPI:    http://localhost:8080/api/v3/api-docs
Health:     http://localhost:8080/api/actuator/health
```

Swagger e OpenAPI são desabilitados no perfil `prod`.

## Autenticação

Envie o access token nas rotas protegidas:

```http
Authorization: Bearer <accessToken>
```

### Login

`POST /api/auth/login` — público

```json
{
  "email": "usuario@sesisenai.org.br",
  "password": "Senha@123"
}
```

Resposta:

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "passwordChangeRequired": false,
  "user": {
    "id": "5e642408-7af3-47ef-a7cb-154aaf4316f7",
    "name": "Nome",
    "username": "nome.usuario",
    "email": "usuario@sesisenai.org.br",
    "role": "ALUNO",
    "status": "ACTIVE",
    "passwordChangeRequired": false,
    "organization": {
      "id": "00000000-0000-4000-8000-000000000001",
      "name": "SENAI"
    }
  }
}
```

### Sessão

| Método | Endpoint | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/auth/login` | Público | Autentica e emite access e refresh tokens |
| `POST` | `/api/auth/refresh` | Público | Rotaciona o refresh token e emite novo par |
| `POST` | `/api/auth/logout` | Autenticado | Revoga o refresh token informado |
| `POST` | `/api/auth/logout-all` | Autenticado | Revoga todos os refresh tokens do usuário |
| `GET` | `/api/auth/me` | Autenticado | Retorna o usuário autenticado |

Body de refresh ou logout:

```json
{
  "refreshToken": "..."
}
```

O refresh token original só é entregue ao cliente. No banco é persistido apenas seu hash SHA-256.

## Roles

Os nomes atuais do projeto foram preservados:

| Requisito | Role da API |
|---|---|
| `ADMIN` | `ADMIN` |
| `COORDINATOR` | `COORDENADOR` |
| `TEACHER` | `PROFESSOR` |
| `STUDENT` | `ALUNO` |

## Criação manual de usuários

`POST /api/users`

Acesso: `ADMIN` ou `COORDENADOR`.

```json
{
  "name": "João da Silva",
  "username": "joao.silva",
  "email": "joao.silva@sesisenai.org.br",
  "role": "ALUNO",
  "organizationId": "00000000-0000-4000-8000-000000000001"
}
```

Regras aplicadas no backend:

- `ADMIN` pode criar `ADMIN`, `COORDENADOR`, `PROFESSOR` e `ALUNO`;
- `COORDENADOR` pode criar apenas `PROFESSOR` e `ALUNO`;
- o coordenador sempre cria na própria organização, independentemente do ID enviado;
- nome de usuário e e-mail são normalizados e devem ser únicos;
- nomes de usuário reservados são rejeitados;
- o domínio do e-mail deve corresponder à organização ativa;
- a senha temporária é aleatória, armazenada somente como hash BCrypt e expira em três dias;
- a conta é criada com troca de senha obrigatória;
- a operação gera auditoria e evento de envio de credenciais após o commit;
- a resposta nunca contém a senha temporária.

As rotas legadas `POST /api/alunos`, `POST /api/professores` e `POST /api/coordenador` foram removidas para impedir que contornem essas regras.

## Importação de usuários por Excel

`POST /api/users/import`

Acesso: `ADMIN` ou `COORDENADOR`. Envie `multipart/form-data` com a parte `file`.

Colunas obrigatórias:

```text
name
username
email
role
organization
```

Somente as roles `PROFESSOR` e `ALUNO` são aceitas, inclusive para administradores. Coordenadores importam exclusivamente para a própria organização. Cada linha valida cabeçalho, conteúdo, e-mail, username, domínio, organização, role e duplicidades no arquivo e no banco.

A resposta contém o UUID da importação, totais e erros por linha. Senhas temporárias nunca são gravadas em `user_import_item`, logs ou auditoria; somente o hash BCrypt permanece no usuário e o valor temporário é entregue ao listener de e-mail após o commit.

## Primeiro acesso, recuperação e perfil

Enquanto `passwordChangeRequired=true`, o backend permite somente:

```text
GET   /api/users/me
PATCH /api/users/me/password
POST  /api/auth/logout
```

Todas as outras operações retornam `403`. A troca de senha valida a senha atual, a política e a confirmação; depois revoga tokens, incrementa a versão de segurança e envia confirmação após o commit.

| Método | Endpoint | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/auth/password/forgot` | Público | Solicita recuperação com resposta sempre genérica |
| `GET` | `/api/auth/password/validate?token=...` | Público | Valida token temporário |
| `POST` | `/api/auth/password/reset` | Público | Redefine a senha com token de uso único |
| `GET` | `/api/users/me` | Autenticado | Consulta o próprio perfil |
| `PATCH` | `/api/users/me` | Autenticado | Altera somente o nome |
| `PATCH` | `/api/users/me/password` | Autenticado | Altera a própria senha |
| `POST` | `/api/users/me/photo` | Autenticado | Envia foto JPEG ou PNG validada |
| `PATCH` | `/api/users/me/preferences` | Autenticado | Altera preferências de notificação |

Tokens de recuperação são aleatórios, expiram, funcionam uma vez e somente o SHA-256 é persistido. A solicitação possui rate limit por IP e e-mail. Fotos usam nome físico aleatório, validação de MIME/assinatura, limite configurável e proteção contra path traversal.

## Administração de usuários

| Método | Endpoint | Acesso |
|---|---|---|
| `PATCH` | `/api/users/{id}/block` | `ADMIN`, `COORDENADOR` |
| `PATCH` | `/api/users/{id}/unblock` | `ADMIN`, `COORDENADOR` |
| `PATCH` | `/api/users/{id}/deactivate` | `ADMIN`, `COORDENADOR` |
| `PATCH` | `/api/users/{id}/reactivate` | `ADMIN`, `COORDENADOR` |
| `PATCH` | `/api/users/{id}/role` | `ADMIN` |
| `POST` | `/api/users/{id}/resend-credentials` | `ADMIN`, `COORDENADOR` |

As mudanças de status exigem:

```json
{
  "reason": "Motivo auditável da alteração"
}
```

A troca de role recebe `{"role":"PROFESSOR"}`. Não é permitido alterar a própria role nem remover o último administrador ativo. O coordenador gerencia apenas `PROFESSOR` e `ALUNO` da própria organização e nunca administra `ADMIN` ou `COORDENADOR`.

As operações administrativas revogam refresh tokens e invalidam imediatamente access tokens anteriores por meio da versão de segurança incluída no JWT. O reenvio substitui o hash da senha anterior, renova a expiração, mantém a troca obrigatória, aplica rate limit e envia a nova credencial somente após o commit.

## Organizações

| Método | Endpoint | Acesso |
|---|---|---|
| `POST` | `/api/organizations` | `ADMIN` |
| `GET` | `/api/organizations` | `ADMIN`, `COORDENADOR` |
| `GET` | `/api/organizations/{id}` | `ADMIN`, `COORDENADOR` |
| `PATCH` | `/api/organizations/{id}` | `ADMIN` |
| `PATCH` | `/api/organizations/{id}/activate` | `ADMIN` |
| `PATCH` | `/api/organizations/{id}/deactivate` | `ADMIN` |

Listagens paginadas aceitam `page`, `size` e `sort`. O tamanho máximo global é 100.

Exemplo de criação:

```json
{
  "name": "SENAI",
  "type": "SENAI",
  "emailDomain": "sesisenai.org.br"
}
```

## Endpoints de domínio existentes

Todos os parâmetros `{id}` abaixo são UUIDs.

### Recursos restritos a `ADMIN`

| Base | Endpoints |
|---|---|
| `/api/equipamento` | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `PATCH /{id}`, `DELETE /{id}` |
| `/api/lugar` | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `PATCH /{id}`, `DELETE /{id}` |
| `/api/maquinas` | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `PATCH /{id}`, `DELETE /{id}` |
| `/api/designacao` | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `PATCH /{id}`, `DELETE /{id}` |
| `/api/material-apoio` | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `PATCH /{id}`, `DELETE /{id}` |
| `/api/turma` | CRUD, `GET /ativos`, `PATCH /{id}/inativar` |
| `/api/alunos` | consultas legadas |
| `/api/professores` | consultas legadas |
| `/api/coordenador` | consultas legadas |
| `/api/coordernador` | alias legado de `/api/coordenador` |

### Recursos que exigem autenticação

| Base | Endpoints |
|---|---|
| `/api/compras` | CRUD e `GET /status/{status}` |
| `/api/solicitao-manutencao` | CRUD |
| `/api/eventos` | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |
| `/api/maquina-log` | CRUD |
| `/api/5s` | CRUD |
| `/api/manutencao-autonoma` | CRUD, `POST /create-all`, `GET /situacao/{situacao}` |
| `/api/notification` | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `PATCH /{id}/read`, `PATCH /{id}/toggle-read`, `PATCH /read-all`, `DELETE /{id}` |

Esses módulos ainda usam parte do modelo legado. As regras de acesso por organização, propriedade do registro e transição de status serão concluídas nas fases de domínio.

## Rotas legadas removidas

| Método | Endpoint | Motivo |
|---|---|---|
| `POST` | `/api/excel/import` | Substituído por `POST /api/users/import` |
| `POST` | `/api/alunos` | Substituído por `POST /api/users` |
| `POST` | `/api/professores` | Substituído por `POST /api/users` |
| `POST` | `/api/coordenador` | Substituído por `POST /api/users` |

As rotas legadas de escrita de aluno, professor e coordenador também foram removidas para impedir alteração direta de senha, status, role ou exclusão física sem as regras e auditoria da Fase 2.

## Identificadores UUID

Todas as entidades JPA e repositories usam `UUID` como identificador. O JSON representa UUID como texto:

```json
{
  "id": "5e642408-7af3-47ef-a7cb-154aaf4316f7"
}
```

Um ID malformado retorna `400 INVALID_PARAMETER`. Quantidades, paginação, contadores e o campo de versão otimista continuam numéricos porque não são identificadores de entidade.

As migrations `V12` e `V13` convertem progressivamente IDs e chaves estrangeiras existentes sem converter números diretamente para UUID. `V14` e `V15` adicionam organizações, segurança, auditoria e refresh tokens. `V16` a `V19` adicionam importações, recuperação de senha, perfil, mídia, preferências e metadados administrativos.

## Respostas de erro

Formato padrão:

```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Existem campos inválidos.",
  "path": "/api/users",
  "timestamp": "2026-07-23T11:00:00",
  "errors": {
    "email": "O e-mail informado é inválido."
  }
}
```

Códigos usados incluem:

```text
VALIDATION_ERROR
INVALID_REQUEST
INVALID_REQUEST_BODY
INVALID_PARAMETER
AUTHENTICATION_REQUIRED
INVALID_CREDENTIALS
CREDENTIALS_EXPIRED
ACCESS_DENIED
RESOURCE_NOT_FOUND
DATA_CONFLICT
CONCURRENT_UPDATE
INVALID_TOKEN
TOKEN_EXPIRED
RATE_LIMIT_EXCEEDED
INVALID_STATE
INVALID_FILE
UNEXPECTED_ERROR
```

Stack traces e detalhes internos não são retornados ao cliente.

## Segurança implementada

- sessão stateless;
- access token JWT HS256 com issuer validado;
- chave JWT mínima de 256 bits;
- refresh token aleatório, com hash persistido, expiração, rotação e revogação;
- logout de uma sessão e de todas as sessões;
- versão de segurança no JWT para invalidar access tokens após senha, role, bloqueio ou inativação;
- bloqueio progressivo após grupos de cinco falhas: 5 minutos, 15 minutos e 1 hora;
- rate limit por IP no endpoint de login;
- rate limit por IP/e-mail na recuperação e por ator/alvo no reenvio de credenciais;
- mensagens genéricas para credenciais inválidas;
- senha temporária e senha definitiva persistidas somente como BCrypt;
- token de recuperação e refresh token persistidos somente como SHA-256;
- CORS com origens explícitas e credenciais;
- Actuator restrito, exceto `health`;
- Swagger desabilitado em produção;
- configuração sensível por variável de ambiente;
- `.env` ignorado pelo Git;
- auditoria para login, criação, importação, senha, status, role e reenvio de credenciais.

## Banco e testes

O Hibernate usa `ddl-auto=validate` fora dos testes e o Flyway é responsável pelo schema. Não altere migrations já aplicadas; crie uma nova migration versionada.

Estado verificado em 23/07/2026:

- migrations `V1` até `V19` aplicadas com sucesso no PostgreSQL 17;
- nenhuma coluna de ID ou foreign key permanece com tipo numérico;
- 32 testes automatizados em 16 classes passando;
- Testcontainers valida o schema final e todas as chaves primárias UUID;
- fluxos integrados cobertos: login, refresh, logout, importação parcial, primeiro acesso, recuperação, perfil, bloqueio, inativação, role, reenvio, auditoria e isolamento por organização.

## Próxima fase

A Fase 3 deve corrigir os domínios críticos: compras, relacionamentos e mappers, Livro Máquina, inspeções, máquinas/equipamentos e validadores de transição de status. Depois devem ser concluídos ocorrências, comentários, histórico, anexos, notificações por evento, filtros, dashboard e exportações.
