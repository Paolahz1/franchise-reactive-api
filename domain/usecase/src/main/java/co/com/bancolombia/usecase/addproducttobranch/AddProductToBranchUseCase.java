package co.com.bancolombia.usecase.addproducttobranch;

import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddProductToBranchUseCase {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public Mono<Product> execute(Long branchId, Product product) {
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND))))
                .flatMap(branch -> 
                    productRepository.findByNameAndBranchId(product.getName(), branchId)
                        .flatMap(existing -> 
                            Mono.<Product>error(new BusinessException(TechnicalMessage.PRODUCT_NAME_DUPLICATE)))
                        .switchIfEmpty(Mono.defer(() -> {
                            product.setBranchId(branchId);
                            return productRepository.save(product);
                        }))
                );
    }
}
