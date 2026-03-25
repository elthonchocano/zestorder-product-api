package com.echocano.zestorder.product.infrastructure.adapter.input.rest.controllers;

import com.echocano.zestorder.product.application.exception.ProductAlreadyExistsException;
import com.echocano.zestorder.product.application.exception.ProductNotFoundException;
import com.echocano.zestorder.product.application.exception.ProductWasDeletedException;
import com.echocano.zestorder.product.application.exception.RepositoryException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micrometer.tracing.Tracer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestControllerAdvice
public class RestExceptionHandler {

    private final Tracer tracer;

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<ErrorDetails> handleRepositoryException(
            RepositoryException ex, ServerWebExchange exchange) {
        ErrorDetails errorDetails = new ErrorDetails(
                ex.getMessage(),
                exchange.getRequest().getPath().value(),
                getTraceId(),
                null,
                LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleProductNotFoundException(
            ProductNotFoundException ex, ServerWebExchange exchange) {
        ErrorDetails errorDetails = new ErrorDetails(
                ex.getMessage(),
                exchange.getRequest().getPath().value(),
                getTraceId(),
                null,
                LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(
            IllegalArgumentException ex, ServerWebExchange exchange) {
        ErrorDetails errorDetails = new ErrorDetails(
                ex.getMessage(),
                exchange.getRequest().getPath().value(),
                getTraceId(),
                null,
                LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleProductAlreadyExistsException(
            ProductAlreadyExistsException ex, HttpServletRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                ex.getMessage(),
                request.getRequestURI(),
                getTraceId(),
                null,
                LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductWasDeletedException.class)
    public ResponseEntity<ErrorDetails> handleProductWasDeletedException(
            ProductWasDeletedException ex, HttpServletRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                ex.getMessage(),
                request.getRequestURI(),
                getTraceId(),
                null,
                LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDetails> handleNoResourceFoundException(NoResourceFoundException ex
            , ServerWebExchange exchange) {
        ErrorDetails errorDetails = new ErrorDetails(
                ex.getReason(),
                exchange.getRequest().getPath().value(),
                getTraceId(),
                null,
                LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleException(Exception ex, ServerWebExchange exchange) {
        ErrorDetails errorDetails = new ErrorDetails(
                ex.getMessage(),
                exchange.getRequest().getPath().value(),
                getTraceId(),
                null,
                LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorDetails> handleMissingBody(ServerWebInputException ex, ServerHttpRequest request) {
        String path = request.getPath().value();
        String cleanMessage = ex.getReason() != null ? ex.getReason() : "Malformed or missing request body";
        ErrorDetails errorDetails = new ErrorDetails(
                cleanMessage,
                path,
                getTraceId(),
                null,
                LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorDetails> handleValidationErrors(WebExchangeBindException ex, ServerHttpRequest request) {
        List<ErrorDetails.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> new ErrorDetails.FieldError(
                        f.getField(),
                        f.getDefaultMessage(),
                        f.getRejectedValue()
                ))
                .toList();

        ErrorDetails response = new ErrorDetails(
                "Data provided is invalid.",
                request.getPath().value(),
                getTraceId(),
                fieldErrors,
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(response);
    }

    private String getTraceId() {
        String traceId;
        if ((tracer.currentSpan() != null)) {
            traceId = Objects.requireNonNull(tracer.currentSpan()).context().traceId();
        } else {
            traceId = "none";
        }
        return traceId;
    }

    @Schema(name = "ErrorResponse", description = "Standard error structure for API failures")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ErrorDetails(
            @Schema(example = "Service Unavailable")
            String message,

            @Schema(example = "uri=/api/v1/products")
            String path,

            @Schema(example = "5f3b2a1c6e7d8f90")
            String traceId,

            List<FieldError> errors,

            @Schema(example = "2026-03-15T12:00:00")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime timestamp
    ) {
        @Schema(name = "FieldError", description = "Standard error field structure")
        public record FieldError(
                @Schema(example = "basePrice")
                String field,
                @Schema(example = "Base price must be greater than zero")
                String message,
                @Schema(example = "-5.00")
                Object rejectedValue
        ) {}
    }
}
