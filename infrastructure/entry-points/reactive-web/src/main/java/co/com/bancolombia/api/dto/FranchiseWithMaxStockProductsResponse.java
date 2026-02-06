package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Franquicia con sus sucursales y el producto con mayor stock de cada sucursal")
public class FranchiseWithMaxStockProductsResponse {

    @Schema(description = "ID de la franquicia", example = "1")
    private Long franchiseId;

    @Schema(description = "Nombre de la franquicia", example = "Starbucks")
    private String franchiseName;

    @Schema(description = "Lista de sucursales con sus productos top")
    private List<BranchWithTopProductResponse> branches;
}