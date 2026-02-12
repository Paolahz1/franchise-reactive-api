package co.com.bancolombia.mysql.dto;

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
public class BranchWithProductDto {
    private Long branchId;
    private String branchName;
    private Long franchiseId;
    private Long productId;
    private String productName;
    private Integer productStock;
    private Long productBranchId;
}
