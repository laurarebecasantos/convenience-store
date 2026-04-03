# Convenience Store API

API REST para gerenciamento de uma conveniência, desenvolvida com Spring Boot. Cobre autenticação com JWT, gestão de usuários, produtos, clientes e vendas.

## Tecnologias

- Java 17
- Spring Boot 3.3.3
- Spring Security + JWT (Auth0 java-jwt 4.4.0)
- Spring Data JPA + Hibernate
- MySQL 8
- Flyway (migrações de banco)
- Lombok
- Maven

---

## Pré-requisitos

- Java 17+
- Maven 3.8+ (ou use o wrapper `./mvnw` incluso)
- MySQL 8 rodando localmente

---

## Configuração local

### 1. Crie o banco de dados no MySQL

```sql
CREATE DATABASE conveniencestore_db;
```

### 2. Configure as variáveis de ambiente

O projeto usa variáveis de ambiente para credenciais sensíveis. Você pode defini-las no sistema ou simplesmente deixar os valores padrão (não recomendado em produção):

| Variável     | Descrição                    | Padrão       |
|--------------|------------------------------|--------------|
| `DB_SECRET`  | Senha do MySQL               | `root1234`   |
| `JWT_SECRET` | Chave secreta para JWT       | `123456789`  |

Para configurar no terminal (Linux/Mac):
```bash
export DB_SECRET=sua_senha_mysql
export JWT_SECRET=sua_chave_jwt_secreta
```

Para configurar no terminal (Windows CMD):
```cmd
set DB_SECRET=sua_senha_mysql
set JWT_SECRET=sua_chave_jwt_secreta
```

> Se preferir, edite diretamente o `src/main/resources/application.properties` para desenvolvimento local.

### 3. Verifique o `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/conveniencestore_db
spring.datasource.username=root
spring.datasource.password=${DB_SECRET:root1234}
api.security.token.secret=${JWT_SECRET:123456789}
```

Ajuste `username` e `url` conforme sua instalação do MySQL.

### 4. Execute a aplicação

```bash
./mvnw spring-boot:run
```

O Flyway executará automaticamente as migrações e criará todas as tabelas na primeira inicialização.

A API ficará disponível em: `http://localhost:8080`

---

## Estrutura do Projeto

```
src/main/java/com/api/rest/conveniencestore/
├── controller/       # Endpoints REST
├── service/          # Regras de negócio
├── repository/       # Acesso a dados (Spring Data JPA)
├── model/            # Entidades JPA
├── dto/              # Objetos de transferência de dados
├── security/         # Configuração do Spring Security
├── enums/            # Enumerações (Status, Roles, PaymentMethod, Category)
├── exceptions/       # Exceções customizadas e handler global
├── validations/      # Validações (CPF, senha, usuário)
└── utils/            # Utilitários e constantes
```

---

## Banco de Dados

As migrações são gerenciadas pelo Flyway e executadas automaticamente:

| Migração | Descrição |
|----------|-----------|
| V1 | Criação da tabela `users` |
| V2 | Criação da tabela `products` |
| V3 | Criação da tabela `sales` |
| V4 | Criação da tabela `sales_products` (pivot) |
| V5 | Constraint de unicidade no nome do produto |
| V6 | Criação da tabela `clients` |
| V7 | Coluna `seller` na tabela `sales` |
| V8 | Criação da tabela `sale_items` (itens por venda, usada para restaurar estoque no cancelamento) |
| V9 | Coluna `version` na tabela `products` (controle de concorrência otimista) |
| V10 | Programa de fidelidade: `points_balance` (clients), `points_earned`/`points_used`/`discount` (sales), tabelas `loyalty_points` e `loyalty_transactions` |

---

## Autenticação

A API usa **JWT (Bearer Token)**. O fluxo é:

1. Registre um usuário via `POST /users`
2. Faça login via `POST /login` e receba o token
3. Inclua o token no header das demais requisições:
   ```
   Authorization: Bearer <seu_token>
   ```

---

## Endpoints

### Autenticação

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/login` | Autentica e retorna JWT | Não |

**Body:**
```json
{
  "username": "seu_usuario",
  "password": "sua_senha"
}
```

---

### Usuários `/users`

| Método | Endpoint | Descrição | Auth | Role |
|--------|----------|-----------|------|------|
| POST | `/users` | Cadastra novo usuário | Não | - |
| GET | `/users/me` | Retorna o usuário autenticado | Sim | qualquer |
| GET | `/users` | Lista usuários ativos | Sim | qualquer |
| PUT | `/users/{id}` | Atualiza username/senha | Sim | qualquer |
| DELETE | `/users/{id}` | Remove usuário | Sim | ADMIN |
| PATCH | `/users/{id}/status` | Altera status (`ACTIVE`/`INACTIVE`) | Sim | ADMIN |
| PATCH | `/users/{id}/roles` | Promove a `ADMIN` | Sim | ADMIN |

**Roles disponíveis:** `USER`, `ADMIN`  
**Status disponíveis:** `ACTIVE`, `INACTIVE`

---

### Clientes `/clients`

| Método | Endpoint | Descrição | Auth | Role |
|--------|----------|-----------|------|------|
| POST | `/clients` | Cadastra novo cliente | Sim | qualquer |
| GET | `/clients` | Lista todos os clientes | Sim | qualquer |
| GET | `/clients/{id}` | Busca cliente por ID (inclui saldo de pontos) | Sim | qualquer |

> O campo `pointsBalance` é retornado no `GET /clients/{id}` e representa o saldo atual de pontos de fidelidade do cliente.

**Body (POST):**
```json
{
  "name": "Nome do Cliente",
  "cpf": "000.000.000-00"
}
```

---

### Produtos `/products`

| Método | Endpoint | Descrição | Auth | Role |
|--------|----------|-----------|------|------|
| POST | `/products` | Cadastra produto | Sim | ADMIN |
| GET | `/products` | Lista todos os produtos | Sim | qualquer |
| PUT | `/products/{id}` | Atualiza produto | Sim | ADMIN |
| PATCH | `/products/{id}/status` | Altera status (`ACTIVE`/`INACTIVE`) | Sim | ADMIN |
| GET | `/products/duedate` | Lista produtos vencidos (até hoje) | Sim | qualquer |
| GET | `/products/expiring?days=7` | Lista produtos próximos de vencer | Sim | qualquer |

> O parâmetro `days` é opcional (padrão: 7). Retorna produtos cuja data de vencimento está entre amanhã e `hoje + days`.

**Categorias:** `FUEL`, `FOOD`, `BEVERAGE`, `CLEANING_PRODUCTS`  
**Status:** `REGISTERED`, `ACTIVE`, `INACTIVE`

---

### Vendas `/sales`

| Método | Endpoint | Descrição | Auth | Role |
|--------|----------|-----------|------|------|
| POST | `/sales` | Registra venda | Sim | qualquer |
| GET | `/sales?paymentMethod=CASH` | Lista vendas por forma de pagamento | Sim | qualquer |
| PATCH | `/sales/{id}/status` | Cancela venda (`CANCELLED`) | Sim | ADMIN |

> Ao registrar uma venda, o campo `seller` é preenchido automaticamente com o username do usuário autenticado. O campo opcional `pointsToUse` permite que o cliente resgate pontos de fidelidade como desconto. A listagem retorna `pointsEarned`, `pointsUsed` e `discount`.

**Body (POST):**
```json
{
  "productIds": [1, 2],
  "quantity": [2, 1],
  "paymentMethod": "CASH",
  "clientCpf": "123.456.789-09",
  "pointsToUse": 500
}
```

> O campo `pointsToUse` é opcional. Quando informado, deve ser múltiplo de 100 (mínimo 100). A conversão é 100 pontos = R$1,00 de desconto, limitado a 50% do valor da compra.

**Formas de pagamento:** `CASH`, `CREDIT`, `DEBIT`  
**Status:** `APPROVED`, `CANCELLED`

---

### Fidelidade `/loyalty`

| Método | Endpoint | Descrição | Auth | Role |
|--------|----------|-----------|------|------|
| POST | `/loyalty/simulate` | Simula resgate de pontos | Sim | qualquer |
| GET | `/loyalty/clients/{id}/transactions` | Extrato de pontos do cliente | Sim | ADMIN |

**Regras do programa:**
- **Acúmulo:** 1 ponto a cada R$1,00 gasto (arredondamento para baixo, sobre valor final após desconto)
- **Resgate:** 100 pontos = R$1,00 de desconto | mínimo: 100 pontos | máximo: 50% do valor da compra
- **Expiração:** pontos expiram após 90 dias da aquisição (por lote/compra)
- **Cancelamento:** pontos são estornados — saldo pode ficar negativo até ser compensado
- **Consumo:** FIFO — lotes mais antigos são consumidos primeiro
- **Auditoria:** todas as operações são registradas com tipo (`EARN`, `REDEEM`, `EXPIRE`, `CANCEL`)

**Body (POST /loyalty/simulate):**
```json
{
  "clientId": 1,
  "purchaseAmount": 200.00,
  "pointsToUse": 500
}
```

**Resposta:**
```json
{
  "pointsToUse": 500,
  "discount": 5.00,
  "finalAmount": 195.00,
  "pointsAfterPurchase": 895
}
```

---

## Exemplo de fluxo completo

```bash
# 1. Cadastrar usuário
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123","email":"admin@loja.com"}'

# 2. Login
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}'
# Resposta: {"token": "eyJ..."}

# 3. Cadastrar produto (com token)
curl -X POST http://localhost:8080/products \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{"name":"Água Mineral","category":"BEVERAGE","price":3.50,"stockQuantity":100,"expirationDate":"2025-12-31"}'

# 4. Registrar venda
curl -X POST http://localhost:8080/sales \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{"paymentMethod":"CASH","products":[{"id":1,"quantity":2}]}'
```

---

## Sobre o Projeto

Este projeto foi desenvolvido com objetivo de aprender e consolidar conhecimentos sobre API REST com Spring Boot, cobrindo:

1. Criação de projeto base
2. Serviço de cadastro de usuários
3. Autenticação com JWT
4. Detalhamento de dados do usuário autenticado
5. Cadastro e gestão de produtos
6. Registro de vendas
7. Controle de estoque
8. Fidelidade e pontuação de clientes
9. Relatórios e estatísticas *(em desenvolvimento)*

Acompanhe o progresso no [Trello do projeto](https://trello.com/b/zd8yvutP/projeto-api-rest-usuario).

---

## Repositório original

Projeto original: [laurarebecasantos/convenience-store](https://github.com/laurarebecasantos/convenience-store)
