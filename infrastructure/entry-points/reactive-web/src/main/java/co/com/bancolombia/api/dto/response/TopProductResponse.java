package co.com.bancolombia.api.dto.response;

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
@Schema(description = "Producto con mayor stock en una sucursal")
public class TopProductResponse {

    @Schema(description = "ID único del producto", example = "1")
    private Long productId;

    @Schema(description = "Nombre del producto", example = "Café Latte")
    private String productName;

    @Schema(description = "Stock disponible", example = "150")
    private Integer stock;
}