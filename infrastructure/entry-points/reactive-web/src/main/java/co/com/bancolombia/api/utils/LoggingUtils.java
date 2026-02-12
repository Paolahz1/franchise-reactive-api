package co.com.bancolombia.api.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingUtils {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void logRequest(String operation, ServerRequest request, Object body) {
        log.info("[REQUEST_START] operation={} method={} params={}",
                operation,
                request.method(),
                request.pathVariables());

        if (log.isDebugEnabled()) {
            log.debug("[REQUEST_BODY] operation={} payload={}",
                    operation,
                    toJson(body));
        }
    }

    public void logRequest(String operation, ServerRequest request) {
        log.info("[REQUEST_START] operation={} method={} params={}",
                operation,
                request.method(),
                request.pathVariables());
    }

    public void logResponse(String operation, Object response, int status) {
        log.info("[REQUEST_END] operation={} status={}",
                operation,
                status);

        if (log.isDebugEnabled()) {
            log.debug("[RESPONSE_BODY] operation={} payload={}",
                    operation,
                    toJson(response));
        }
    }

    public void logResponse(String operation, int status) {
        log.info("[REQUEST_END] operation={} status={}",
                operation,
                status);
    }

    public void logError(String operation, Throwable error) {
        log.error("[REQUEST_ERROR] operation={} exception={} message={}",
                operation,
                error.getClass().getSimpleName(),
                error.getMessage());
    }

    private String toJson(Object obj) {
        if (obj == null) return "null";
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }
}
