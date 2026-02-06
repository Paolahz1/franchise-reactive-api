package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response de un producto")
public class ProductResponse {

    @Schema(description = "ID único del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Café Latte")
    private String name;

    @Schema(description = "Stock disponible del producto", example = "100")
    private Integer stock;

    @Schema(description = "ID de la sucursal a la que pertenece", example = "1")
    private Long branchId;
}
