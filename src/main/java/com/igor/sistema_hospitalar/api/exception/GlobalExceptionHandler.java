package com.igor.sistema_hospitalar.api.exception;

import com.igor.sistema_hospitalar.domain.exception.BusinessException;
import com.igor.sistema_hospitalar.domain.exception.ResourceConflictException;
import com.igor.sistema_hospitalar.domain.exception.ResourceNotFoundException;
import com.igor.sistema_hospitalar.domain.exception.TooManyRequestsException;
import com.igor.sistema_hospitalar.domain.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceConflictException(ResourceConflictException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", "Recurso não encontrado.", request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ApiErrorResponse> handleTooManyRequestsException(TooManyRequestsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.joining(", "));
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Erro de validação: " + message, request);
    }

    @ExceptionHandler(org.springframework.data.mapping.PropertyReferenceException.class)
    public ResponseEntity<ApiErrorResponse> handlePropertyReferenceException(org.springframework.data.mapping.PropertyReferenceException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Propriedade de ordenação inválida: " + ex.getPropertyName(), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Parâmetro obrigatório ausente: " + ex.getParameterName(), request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed", "Método HTTP não suportado: " + ex.getMethod(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Ocorreu um erro inesperado no servidor.", request);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(jakarta.validation.ConstraintViolationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Parâmetro inválido: " + ex.getMessage(), request);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolationException(org.springframework.dao.DataIntegrityViolationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", "Conflito de dados: O recurso já existe ou viola uma restrição única.", request);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(org.springframework.http.converter.HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Formato de dados inválido na requisição. Verifique os tipos de dados e valores informados.", request);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Parâmetro inválido: " + ex.getName() + ". Verifique o tipo do valor informado.", request);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String error, String message, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
