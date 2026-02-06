package co.com.bancolombia.usecase.removeproductfrombranch;

import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RemoveProductFromBranchUseCase {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public Mono<Void> execute(Long branchId, Long productId) {
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND))))
                .flatMap(branch -> 
                    productRepository.findById(productId)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND))))
                        .flatMap(product -> 
                            Mono.just(product.getBranchId())
                                .filter(productBranchId -> productBranchId.equals(branchId))
                                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.PRODUCT_REMOVAL_ERROR))))
                                .flatMap(validBranchId -> productRepository.deleteById(productId))
                        )
                );
    }
}
