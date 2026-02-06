package co.com.bancolombia.model.common.exceptions;

import co.com.bancolombia.model.common.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final TechnicalMessage technicalMessage;

    public BusinessException(TechnicalMessage technicalMessage) {
        super(technicalMessage.getMessage());
        this.technicalMessage = technicalMessage;
    }

    public String getCode() {
        return technicalMessage.getCode();
    }
}
