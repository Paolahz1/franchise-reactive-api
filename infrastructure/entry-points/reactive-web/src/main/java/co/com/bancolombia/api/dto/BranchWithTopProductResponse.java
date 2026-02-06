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
@Schema(description = "Sucursal con su producto de mayor stock")
public class BranchWithTopProductResponse {

    @Schema(description = "ID de la sucursal", example = "1")
    private Long branchId;

    @Schema(description = "Nombre de la sucursal", example = "Starbucks Centro")
    private String branchName;

    @Schema(description = "Producto con mayor stock en esta sucursal")
    private TopProductResponse topProduct;
}