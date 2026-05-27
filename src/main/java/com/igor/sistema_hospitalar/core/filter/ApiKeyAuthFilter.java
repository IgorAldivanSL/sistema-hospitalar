package com.igor.sistema_hospitalar.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.igor.sistema_hospitalar.domain.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Ignorar swagger, rotas abertas e o frontend nativo
        String path = request.getRequestURI();
        if (path.equals("/") || path.equals("/index.html") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/h2-console") || path.startsWith("/api/v1/auth/keys")) {
            filterChain.doFilter(request, response);
            return;
        }

        String reqApiKey = request.getHeader("X-API-Key");
        if (reqApiKey != null && apiKeyService.isValid(reqApiKey)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Acesso nao autorizado. Chave de API invalida.");
        }
    }
}
