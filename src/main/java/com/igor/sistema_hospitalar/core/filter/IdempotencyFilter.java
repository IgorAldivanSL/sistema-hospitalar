package com.igor.sistema_hospitalar.core.filter;

import com.igor.sistema_hospitalar.domain.entity.IdempotencyRecord;
import com.igor.sistema_hospitalar.domain.exception.BusinessException;
import com.igor.sistema_hospitalar.domain.service.IdempotencyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Optional;

@Component
public class IdempotencyFilter extends OncePerRequestFilter {

    private final IdempotencyService idempotencyService;
    private final HandlerExceptionResolver resolver;

    public IdempotencyFilter(IdempotencyService idempotencyService, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.idempotencyService = idempotencyService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String idempotencyKey = request.getHeader("X-Idempotency-Key");
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

        byte[] requestBody = cachedRequest.getCachedBody();
        String requestHash = idempotencyService.generateHash(requestBody);

        Optional<IdempotencyRecord> existingRecordOpt = idempotencyService.findByKey(idempotencyKey);

        if (existingRecordOpt.isPresent()) {
            IdempotencyRecord existingRecord = existingRecordOpt.get();

            if (!existingRecord.getRequestHash().equals(requestHash)) {
                resolver.resolveException(request, response, null, new BusinessException("Chave de idempotência já utilizada com um payload diferente."));
                return;
            }

            response.setStatus(existingRecord.getResponseStatus());
            response.setContentType("application/json;charset=UTF-8");
            if (existingRecord.getResponseBody() != null) {
                response.getWriter().write(existingRecord.getResponseBody());
            }
            return;
        }

        filterChain.doFilter(cachedRequest, cachingResponse);

        int status = cachingResponse.getStatus();
        byte[] responseBodyBytes = cachingResponse.getContentAsByteArray();
        String responseBodyStr = new String(responseBodyBytes, cachingResponse.getCharacterEncoding());

        idempotencyService.saveRecord(idempotencyKey, requestHash, status, responseBodyStr);

        cachingResponse.copyBodyToResponse();
    }
}
