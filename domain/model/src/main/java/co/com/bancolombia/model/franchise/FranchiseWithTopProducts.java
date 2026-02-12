package co.com.bancolombia.model.franchise;

import co.com.bancolombia.model.branch.BranchWithTopProduct;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FranchiseWithTopProducts {
    private Franchise franchise;
    private List<BranchWithTopProduct> branchesWithTopProducts;
}
