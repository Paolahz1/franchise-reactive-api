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
@Schema(description = "Product response with branch information")
public class ProductWithBranchResponse {

    @Schema(description = "Unique product ID", example = "1")
    private Long productId;

    @Schema(description = "Product name", example = "Café Latte")
    private String productName;

    @Schema(description = "Available product stock", example = "150")
    private Integer stock;

    @Schema(description = "Branch ID", example = "1")
    private Long branchId;

    @Schema(description = "Branch name", example = "Starbucks Downtown")
    private String branchName;
}
