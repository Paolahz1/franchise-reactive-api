package co.com.bancolombia.api.config;

import co.com.bancolombia.api.dto.ErrorResponse;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(-2)
public class GlobalErrorHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof BusinessException businessException) {
            return handleBusinessException(exchange, businessException);
        }
        return handleGenericException(exchange, ex);
    }

    private Mono<Void> handleBusinessException(ServerWebExchange exchange, BusinessException ex) {
        log.warn("Business error: {} - {}", ex.getCode(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();

        return writeResponse(exchange, errorResponse, HttpStatus.BAD_REQUEST);
    }

    private Mono<Void> handleGenericException(ServerWebExchange exchange, Throwable ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                .build();

        return writeResponse(exchange, errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Mono<Void> writeResponse(ServerWebExchange exchange, ErrorResponse errorResponse, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = String.format("{\"code\":\"%s\",\"message\":\"%s\"}", 
                errorResponse.getCode(), 
                errorResponse.getMessage());
        
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
