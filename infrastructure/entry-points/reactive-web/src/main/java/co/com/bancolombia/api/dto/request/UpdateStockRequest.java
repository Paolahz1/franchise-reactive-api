package co.com.bancolombia.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para actualizar el stock de un producto")
public class UpdateStockRequest {

    @NotNull(message = "Stock is mandatory")
    @PositiveOrZero(message = "Stock must be zero or positive")
    @Schema(description = "Stock", example = "100", required = true, minimum = "0")
    private Integer stock;
}