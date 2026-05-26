package com.igor.sistema_hospitalar.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.data.web.PagedResourcesAssembler;

@Configuration
public class OpenApiConfig {

    static {
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(PagedResourcesAssembler.class);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("API Sistema Hospitalar - Implementação de Requisitos Avançados RESTful")
                        .version("v1.0")
                        .description(
                                "API RESTful desenvolvida para a gestão de um ambiente hospitalar, englobando todas as especificações e requisitos avançados do projeto final.\n\n"
                                        +
                                        "### Requisitos Técnicos Implementados:\n" +
                                        "- **Arquitetura e Persistência:** Spring Boot, Java, Maven, H2 Database e Spring Data JPA com 6 Entidades Mapeadas.\n"
                                        +
                                        "- **Operações e Relacionamentos:** Endpoints de CRUD completos, consultas paginadas e customizadas, abrangendo relacionamentos complexos (1:1, 1:N, N:M).\n"
                                        +
                                        "- **Validação e Tratamento de Erros:** Validação estrita com Bean Validation e tratamento global de exceções via `@ControllerAdvice` com os respectivos códigos de status HTTP apropriados.\n"
                                        +
                                        "- **HATEOAS:** API altamente navegável com respostas enriquecidas de links relevantes (utilizando EntityModel e PagedModel).\n"
                                        +
                                        "- **Idempotência:** Garantia de processamento único para operações POST via Header personalizado `X-Idempotency-Key`.\n"
                                        +
                                        "- **Segurança por API Key:** Criação, gerenciamento e proteção de endpoints sensíveis exigindo a presença de uma chave válida no Header `X-API-Key`.\n"
                                        +
                                        "- **Rate Limiting:** Limitação inteligente de taxa de requisições por IP para proteção contra sobrecarga, retornando HTTP `429 Too Many Requests` e Header `Retry-After`.\n"
                                        +
                                        "- **CORS:** Políticas de `Cross-Origin Resource Sharing` estritamente configuradas para permitir acesso de origens específicas.\n"
                                        +
                                        "- **Versionamento da API:** Suporte flexível e demonstração de múltiplas versões operando simultaneamente via Header `X-API-Version`.")
                        .contact(new io.swagger.v3.oas.models.info.Contact().name("Igor Aldivan")
                                .email("igoraldivansl@gmail.com")))
                .components(new Components()
                        .addSecuritySchemes("ApiKeyAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")))
                .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"));
    }

    @Bean
    public org.springdoc.core.customizers.OpenApiCustomizer globalResponsesCustomizer() {
        return openApi -> openApi.getPaths().values()
                .forEach(pathItem -> pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                    if (httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.POST) {
                        if (operation.getParameters() == null) {
                            operation.setParameters(new java.util.ArrayList<>());
                        }
                        boolean hasIdempotencyKey = operation.getParameters().stream()
                                .anyMatch(p -> "X-Idempotency-Key".equals(p.getName()));
                        if (!hasIdempotencyKey) {
                            operation.getParameters().add(new io.swagger.v3.oas.models.parameters.HeaderParameter()
                                    .name("X-Idempotency-Key")
                                    .description("Chave de idempotência (UUID) para garantir que a requisição não seja processada em duplicidade.")
                                    .required(false)
                                    .schema(new io.swagger.v3.oas.models.media.StringSchema()));
                        }
                    }
                    
                    io.swagger.v3.oas.models.responses.ApiResponses apiResponses = operation.getResponses();
                    if (!apiResponses.containsKey("400")) {
                        apiResponses.addApiResponse("400",
                                new io.swagger.v3.oas.models.responses.ApiResponse().description("Dados inválidos"));
                    }
                    if (!apiResponses.containsKey("401")) {
                        apiResponses.addApiResponse("401",
                                new io.swagger.v3.oas.models.responses.ApiResponse().description("Não autenticado"));
                    }
                    // Não documentamos 404 para operações POST pois POST cria novos recursos em vez
                    // de buscá-los na raiz do endpoint.
                    if (httpMethod != io.swagger.v3.oas.models.PathItem.HttpMethod.POST
                            && !apiResponses.containsKey("404")) {
                        apiResponses.addApiResponse("404", new io.swagger.v3.oas.models.responses.ApiResponse()
                                .description("Recurso não encontrado"));
                    }
                    // 409 (Conflict) ocorre apenas em criações (POST) ou atualizações (PUT) que
                    // violam integridade de dados (ex: CPF já existe)
                    if ((httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.POST
                            || httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.PUT)
                            && !apiResponses.containsKey("409")) {
                        apiResponses.addApiResponse("409", new io.swagger.v3.oas.models.responses.ApiResponse()
                                .description("Conflito: Recurso já existe ou viola restrição única"));
                    }
                    
                    if (httpMethod == io.swagger.v3.oas.models.PathItem.HttpMethod.POST && !apiResponses.containsKey("422")) {
                        apiResponses.addApiResponse("422", new io.swagger.v3.oas.models.responses.ApiResponse()
                                .description("Entidade não processável: Chave de idempotência enviada foi reutilizada com um payload (corpo da requisição) diferente."));
                    }

                    if (!apiResponses.containsKey("429")) {
                        apiResponses.addApiResponse("429",
                                new io.swagger.v3.oas.models.responses.ApiResponse().description("Muitas requisições"));
                    }
                }));
    }
}
