# Portal de Manutenção — API

API REST do Portal de Manutenção, construída com Java 21, Spring Boot 4, Spring Security, JWT, Spring Data JPA, PostgreSQL e Flyway.

O estado atual corresponde à conclusão da base de segurança da Fase 1: identificadores UUID, organizações, autenticação stateless, refresh token rotativo, logout, proteção contra tentativas de login, RBAC inicial, criação manual controlada de usuários e respostas de erro padronizadas.

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

`JWT_SECRET` deve ser um valor Base64 que represente pelo menos 32 bytes aleatórios. Não armazene senhas, tokens ou segredos no repositório.

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

As rotas legadas `POST /api/alunos`, `POST /api/professores` e `POST /api/coordenador` estão bloqueadas para impedir que contornem essas regras.

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
| `/api/alunos` | consultas, edição, inativação e exclusão legadas |
| `/api/professores` | consultas, edição, inativação e exclusão legadas |
| `/api/coordenador` | consultas, edição, inativação e exclusão legadas |
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

## Rotas temporariamente indisponíveis

| Método | Endpoint | Motivo |
|---|---|---|
| `POST` | `/api/excel/import` | Importador legado bloqueado até aplicar validação por linha, role e organização |
| `POST` | `/api/alunos` | Substituído por `POST /api/users` |
| `POST` | `/api/professores` | Substituído por `POST /api/users` |
| `POST` | `/api/coordenador` | Substituído por `POST /api/users` |

Os caminhos de recuperação de senha estão reservados na configuração de segurança, mas seus controllers serão implementados na Fase 2:

```text
POST /api/auth/password/forgot
GET  /api/auth/password/validate
POST /api/auth/password/reset
```

## Identificadores UUID

Todas as 21 entidades JPA e todos os repositories usam `UUID`. O JSON representa UUID como texto:

```json
{
  "id": "5e642408-7af3-47ef-a7cb-154aaf4316f7"
}
```

Um ID malformado retorna `400 INVALID_PARAMETER`. Quantidades, paginação, contadores e o campo de versão otimista continuam numéricos porque não são identificadores de entidade.

As migrations `V12` e `V13` convertem progressivamente IDs e chaves estrangeiras existentes sem converter números diretamente para UUID. As migrations `V14` e `V15` adicionam organizações, estado de segurança, auditoria e refresh tokens.

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
UNEXPECTED_ERROR
```

Stack traces e detalhes internos não são retornados ao cliente.

## Segurança implementada

- sessão stateless;
- access token JWT HS256 com issuer validado;
- chave JWT mínima de 256 bits;
- refresh token aleatório, com hash persistido, expiração, rotação e revogação;
- logout de uma sessão e de todas as sessões;
- bloqueio progressivo após grupos de cinco falhas: 5 minutos, 15 minutos e 1 hora;
- rate limit por IP no endpoint de login;
- mensagens genéricas para credenciais inválidas;
- senhas legadas recriptografadas com BCrypt nas rotas de edição;
- CORS com origens explícitas e credenciais;
- Actuator restrito, exceto `health`;
- Swagger desabilitado em produção;
- configuração sensível por variável de ambiente;
- `.env` ignorado pelo Git;
- auditoria base para login e criação de usuário.

## Banco e testes

O Hibernate usa `ddl-auto=validate` fora dos testes e o Flyway é responsável pelo schema. Não altere migrations já aplicadas; crie uma nova migration versionada.

Estado verificado em 23/07/2026:

- migrations `V1` até `V15` aplicadas com sucesso no PostgreSQL;
- nenhuma coluna de ID ou foreign key permanece com tipo numérico;
- 16 testes automatizados passando;
- fluxo integrado coberto: login, refresh, acesso autenticado e logout;
- cobertura unitária da geração/hash de token, bloqueio de login, senha temporária, criação manual e matriz de permissões.

## Próxima fase

A Fase 2 deve implementar:

1. importação Excel segura para `PROFESSOR` e `ALUNO`;
2. histórico de importações e erros por linha;
3. troca obrigatória no primeiro acesso;
4. recuperação de senha com token de uso único;
5. revogação de sessões após troca de senha;
6. perfil do usuário;
7. bloqueio, inativação, reativação e alteração administrativa de role;
8. reenvio controlado de credenciais.

Depois disso, os módulos de compras, Livro Máquina, inspeções, máquinas, equipamentos, ocorrências, comentários, mídia, notificações, filtros, dashboard e exportações devem ser evoluídos conforme as fases descritas nos requisitos.
