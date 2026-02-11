package co.com.bancolombia.api.dto.response;

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
@Schema(description = "Franchise with its branches and the highest stock product of each branch")
public class FranchiseWithMaxStockProductsResponse {

    @Schema(description = "Franchise ID", example = "1")
    private Long franchiseId;

    @Schema(description = "Franchise name", example = "Starbucks")
    private String franchiseName;

    @Schema(description = "List of branches with their top products")
    private List<BranchWithTopProductResponse> branches;
}
