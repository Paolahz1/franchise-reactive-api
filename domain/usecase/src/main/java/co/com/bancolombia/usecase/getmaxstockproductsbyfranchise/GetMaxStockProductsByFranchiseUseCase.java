package co.com.bancolombia.usecase.getmaxstockproductsbyfranchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetMaxStockProductsByFranchiseUseCase {

    private final FranchiseRepository franchiseRepository;
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public Flux<ProductWithBranch> execute(Long franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND))))
                .flatMapMany(franchise ->
                    productRepository.findMaxStockByFranchise(franchiseId)
                        .flatMap(product ->
                            branchRepository.findById(product.getBranchId())
                                .map(branch -> ProductWithBranch.builder()
                                        .product(product)
                                        .branch(branch)
                                        .build())
                        )
                );
    }

    @lombok.Getter
    @lombok.Builder
    public static class ProductWithBranch {
        private Product product;
        private Branch branch;
    }
}