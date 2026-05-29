package com.igor.sistema_hospitalar.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.igor.sistema_hospitalar.domain.service.ApiKeyService;
import com.igor.sistema_hospitalar.domain.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;
    private final HandlerExceptionResolver resolver;

    public ApiKeyAuthFilter(ApiKeyService apiKeyService, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.apiKeyService = apiKeyService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Ignorar swagger, rotas abertas e o frontend nativo
        String path = request.getRequestURI();
        if (path.equals("/") || path.equals("/index.html") || path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/h2-console") || path.startsWith("/api/v1/auth/keys") || path.startsWith("/api/auth/keys")) {
            filterChain.doFilter(request, response);
            return;
        }

        String reqApiKey = request.getHeader("X-API-Key");
        if (reqApiKey != null && apiKeyService.isValid(reqApiKey)) {
            filterChain.doFilter(request, response);
        } else {
            resolver.resolveException(request, response, null, new UnauthorizedException("Acesso não autorizado. Chave de API inválida."));
        }
    }
}
