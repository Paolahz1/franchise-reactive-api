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
@Schema(description = "Response de un producto con información de su sucursal")
public class ProductWithBranchResponse {

    @Schema(description = "ID único del producto", example = "1")
    private Long productId;

    @Schema(description = "Nombre del producto", example = "Café Latte")
    private String productName;

    @Schema(description = "Stock disponible del producto", example = "150")
    private Integer stock;

    @Schema(description = "ID de la sucursal", example = "1")
    private Long branchId;

    @Schema(description = "Nombre de la sucursal", example = "Starbucks Centro")
    private String branchName;
}