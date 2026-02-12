package co.com.bancolombia.api.utils;


import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ValidationUtils {

    private final Validator validator;

    public <T> Mono<T> validate(T object) {
        return Mono.just(validator.validate(object))
                .flatMap(violations ->
                       violations.isEmpty()
                               ? Mono.just(object)
                               : Mono.defer( () -> Mono.error(
                                       new BusinessException(
                                               TechnicalMessage.VALIDATION_ERROR,
                                               violations.stream()
                                                       .map(ConstraintViolation::getMessage)
                                                       .collect(Collectors.joining(", "))
                                       ))
                                )

                );
    }
}
