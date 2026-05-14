# Sistema Hospitalar - API RESTful (Spring Boot)

Este é o projeto final para o curso de Desenvolvimento de APIs com Spring Boot, implementando um "Sistema Hospitalar" completo com diversos requisitos avançados.

## 🚀 Tecnologias e Ferramentas
- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- Banco de Dados H2 (Em memória)
- Bean Validation
- Spring HATEOAS
- Springdoc OpenAPI (Swagger 3)
- Bucket4j (Rate Limiting)
- Maven

## 🛠 Funcionalidades e Requisitos Atendidos

### Parte 1
- **Entidades:** 5 entidades (`Paciente`, `Medico`, `Especialidade`, `Consulta`, `Prontuario`) com relacionamentos 1:1, 1:N e N:N.
- **Enums:** Utilizados (`StatusConsulta` e `TipoSanguineo`).
- **Endpoints:** Mínimo de 5 rotas por entidade (CRUD + Busca customizada).
- **Paginação:** Todas as listagens (`GET`) possuem paginação.
- **HATEOAS:** Links adicionados nas respostas utilizando o `WebMvcLinkBuilder` do Spring.
- **Documentação:** Swagger UI disponível para explorar todos os endpoints de forma visual.

### Parte 2 (Recursos Avançados)
- **Idempotência:** Implementado `IdempotencyFilter` que checa o Header `X-Idempotency-Key` em rotas `POST`.
- **Autenticação (API Key):** Implementado `ApiKeyAuthFilter` que exige o envio do Header `X-API-Key` (Chave: `my-secret-api-key-12345`).
- **Rate Limiting:** Implementado `RateLimitFilter` utilizando `Bucket4j` (Máximo de 10 requisições por minuto por IP).
- **CORS:** Configurado `CorsConfig` liberando os principais verbos HTTP.
- **Versionamento:** Aplicado no `PacienteController`, onde o endpoint `GET /api/v1/pacientes` suporta `X-API-Version=1` e `X-API-Version=2` no cabeçalho.
- **Tratamento de Exceções:** Implementado `GlobalExceptionHandler` interceptando `ResourceNotFoundException`, `BusinessException` e `MethodArgumentNotValidException`.

## 📦 Como rodar o projeto

1. **Clone o repositório** e entre na pasta do projeto.
2. **Execute o Maven**:
   ```bash
   ./mvnw spring-boot:run
   ```
3. **Acesse a Documentação (Swagger)**:
   Abra no seu navegador: `http://localhost:8080/swagger-ui.html`

## 🩺 Testando a API
A API exige a API Key nas requisições. No Swagger, ou Postman, passe no Header:
- `X-API-Key: my-secret-api-key-12345`

Para testes de POST, passe opcionalmente a Idempotência:
- `X-Idempotency-Key: uuid-qualquer`

Arquivo da coleção do **Postman** (`sistema-hospitalar-postman.json`) encontra-se na raiz do projeto.
