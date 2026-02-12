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
@Schema(description = "Product response")
public class ProductResponse {

    @Schema(description = "Unique product ID", example = "1")
    private Long id;

    @Schema(description = "Product name", example = "Café Latte")
    private String name;

    @Schema(description = "Available product stock", example = "100")
    private Integer stock;

    @Schema(description = "ID of the branch it belongs to", example = "1")
    private Long branchId;
}
