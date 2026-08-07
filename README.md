Portal da Manutenção — API

Backend do Portal da Manutenção Mecânica, sistema desenvolvido para centralizar rotinas de manutenção, ativos, usuários, compras, inspeções, 5S e auditoria em um ambiente integrado entre WEG e SENAI.

Status: projeto concluído — release 1.0.0.

Sobre o projeto

A API concentra as regras de negócio, autenticação, autorização, persistência, auditoria e integrações utilizadas pelo portal. O projeto foi desenvolvido em Java com Spring Boot, PostgreSQL e Redis.

Backend: https://github.com/Portal-Manutencao-Mecanica/Manutencao-API

Frontend: https://github.com/Portal-Manutencao-Mecanica/Portal_Mecanica-APP

Funcionalidades

Autenticação e segurança

autenticação com JWT;

access token e refresh token;

logout com revogação de refresh token;

primeiro acesso com senha temporária;

troca obrigatória e expiração da senha inicial;

código de verificação para primeiro acesso;

recuperação de senha por token temporário;

bloqueio após tentativas inválidas;

ativação e inativação de contas;

RBAC por perfil;

rate limiting em operações críticas;

auditoria de operações sensíveis.

Usuários, organizações e turmas

Perfis disponíveis:

Perfil

Responsabilidade geral

ADMIN

Administração global e operações privilegiadas

COORDENADOR

Gestão da organização, usuários e aprovações

PROFESSOR

Acompanhamento de turmas e fluxos de manutenção

ALUNO

Registro e acompanhamento das atividades permitidas

O sistema também possui:

cadastro, edição, exclusão e inativação de usuários;

organizações WEG, SENAI e outros tipos previstos pelo domínio;

associação de professores e alunos a turmas;

importação de usuários por CSV;

histórico das importações;

reemissão de credenciais.

Máquinas, equipamentos e locais

cadastro e gerenciamento de máquinas;

patrimônio e TAG;

condição operacional;

local de instalação;

histórico de manutenção;

equipamentos e materiais;

código SAP;

quantidade disponível;

preço unitário;

mídias associadas.

Solicitações de manutenção

abertura de ocorrências;

prioridade e descrição;

máquina, patrimônio, TAG e local;

alunos participantes;

professor responsável;

aprovação/reprovação pelo professor;

aprovação/reprovação pelo coordenador;

ordem de serviço;

histórico de alterações;

anexos e evidências;

finalização da solicitação.

Outros módulos

manutenção autônoma;

calendário e eventos;

Livro de Máquina;

solicitações de compra e itens;

inconvenientes 5S;

material de apoio;

notificações e preferências;

upload e relacionamento de mídias;

histórico e auditoria.

Tecnologias

Tecnologia

Uso

Java 21

Linguagem principal

Spring Boot 3.5

Framework

Spring Web

API REST

Spring Data JPA / Hibernate

Persistência

Spring Security

Autenticação e autorização

OAuth2 Resource Server

JWT

PostgreSQL 16

Banco de dados

Flyway

Migrations

Redis 7

Infraestrutura distribuída

Caffeine

Cache local

Bucket4j

Rate limiting

Spring Mail

E-mails

Springdoc OpenAPI

Swagger

Maven

Build

Docker / Compose

Infraestrutura local

Arquitetura

Frontend
   │
   ▼
Controllers
   │
   ▼
Services / Regras de negócio
   ├── Segurança
   ├── Eventos
   ├── Auditoria
   └── Validações
   │
   ▼
Repositories
   │
   ├── PostgreSQL
   └── Redis

O projeto utiliza DTOs, eventos de domínio, tratamento centralizado de exceções, Flyway, variáveis de ambiente e organização modular por domínio.

Pré-requisitos

Java 21;

Docker e Docker Compose;

Git.

O projeto possui Maven Wrapper, portanto não é necessário instalar Maven globalmente.

Configuração

Crie um .env com base no .env.example.

Principais variáveis:

POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=

REDIS_PASSWORD=

JWT_SECRET=

APP_MASTER_ADMIN_EMAIL=
APP_MASTER_ADMIN_USERNAME=
APP_MASTER_ADMIN_PASSWORD=

MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_FROM=

APP_FRONTEND_URL=

HOST_DB_PORT=
HOST_REDIS_PORT=

Para seeds de desenvolvimento, consulte também SEED_TEST_USERS.

Nunca versione .env, senhas, tokens, credenciais SMTP ou chaves JWT.

Executando localmente

git clone https://github.com/Portal-Manutencao-Mecanica/Manutencao-API.git
cd Manutencao-API

Suba PostgreSQL e Redis:

docker compose up -d db redis

Linux/macOS:

./mvnw spring-boot:run

Windows:

.\mvnw.cmd spring-boot:run

A API é utilizada pelo frontend em:

http://localhost:8080/api

Swagger / OpenAPI

Swagger UI:

http://localhost:8080/swagger-ui/index.html

OpenAPI JSON:

http://localhost:8080/v3/api-docs

Banco de dados

O schema é controlado pelo Flyway. As migrations são aplicadas durante a inicialização e devem ser tratadas como fonte de verdade da estrutura do banco.

Não altere migrations já aplicadas em ambientes compartilhados.

Testes e build

Testes:

./mvnw test

Build:

./mvnw clean package

O artefato gerado fica em:

target/

Segurança

Entre os controles implementados estão:

BCrypt para senhas;

JWT + refresh token;

autorização por perfil;

sessão stateless;

CORS;

rate limiting;

bloqueio por tentativas inválidas;

tokens de recuperação de uso único;

credenciais temporárias;

proteção de endpoints e mídias;

validação de importações;

auditoria;

segredos externos ao código.

Documentação complementar:

docs/

Estrutura geral

Manutencao-API/
├── .github/
├── docs/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── .env.example
├── compose.yaml
├── Dockerfile
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md

Integração com o frontend

Navegador
   │
   ▼
Next.js /api/*
   │
   ▼
Spring Boot /api/*
   │
   ├── PostgreSQL
   └── Redis

Frontend: https://github.com/Portal-Manutencao-Mecanica/Portal_Mecanica-APP

Status final

A release 1.0.0 marca a conclusão do backend do Portal da Manutenção. A etapa final incluiu hardening, revisão de segurança, limpeza de arquivos locais e consolidação da documentação para entrega.

Desenvolvido como parte do Portal da Manutenção Mecânica — WEG / SENAI.
