package co.com.bancolombia.model.branch;

import co.com.bancolombia.model.product.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BranchWithTopProduct {
    private Branch branch;
    private Product topProduct;
}
