package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para agregar un producto a una sucursal")
public class ProductRequest {

    @Schema(description = "Nombre del producto", example = "Café Latte", required = true)
    private String name;

    @Schema(description = "Stock inicial del producto", example = "100", required = true, minimum = "0")
    private Integer stock;
}
