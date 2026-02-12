package co.com.bancolombia.model.common.exceptions;

import co.com.bancolombia.model.common.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final TechnicalMessage technicalMessage;
    private final String detail;

    public BusinessException(TechnicalMessage technicalMessage, String detail){
        super(technicalMessage.getMessage());
        this.technicalMessage = technicalMessage;
        this.detail = detail;
    }


    public BusinessException(TechnicalMessage technicalMessage) {
        this(technicalMessage, null);
    }


    public String getCode() {
        return technicalMessage.getCode();
    }
}
