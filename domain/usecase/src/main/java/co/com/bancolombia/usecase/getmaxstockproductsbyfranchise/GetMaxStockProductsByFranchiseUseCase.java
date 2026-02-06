package co.com.bancolombia.usecase.getmaxstockproductsbyfranchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class GetMaxStockProductsByFranchiseUseCase {

    private final FranchiseRepository franchiseRepository;
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public Mono<FranchiseWithTopProducts> execute(Long franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND))))
                .flatMap(franchise ->
                    productRepository.findMaxStockByFranchise(franchiseId)
                        .flatMap(product ->
                            branchRepository.findById(product.getBranchId())
                                .map(branch -> BranchWithTopProduct.builder()
                                        .branch(branch)
                                        .topProduct(product)
                                        .build())
                        )
                        .collectList()
                        .map(branchesWithProducts -> FranchiseWithTopProducts.builder()
                                .franchise(franchise)
                                .branchesWithTopProducts(branchesWithProducts)
                                .build())
                );
    }

    @lombok.Getter
    @lombok.Builder
    public static class FranchiseWithTopProducts {
        private Franchise franchise;
        private List<BranchWithTopProduct> branchesWithTopProducts;
    }

    @lombok.Getter
    @lombok.Builder
    public static class BranchWithTopProduct {
        private Branch branch;
        private Product topProduct;
    }
}