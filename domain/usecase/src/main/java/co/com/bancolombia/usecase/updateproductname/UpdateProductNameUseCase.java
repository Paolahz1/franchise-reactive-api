package co.com.bancolombia.usecase.updateproductname;

import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductNameUseCase {

    private final ProductRepository productRepository;

    public Mono<Product> execute(Long productId, String newName) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND))))
                .flatMap(product ->
                    productRepository.findByNameAndBranchId(newName.trim(), product.getBranchId())
                        .flatMap(existing ->
                            existing.getId().equals(productId)
                                ? productRepository.updateName(productId, newName.trim())
                                    .then(productRepository.findById(productId))
                                : Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NAME_DUPLICATE)))
                        )
                        .switchIfEmpty(
                            productRepository.updateName(productId, newName.trim())
                                .then(productRepository.findById(productId))
                        )
                );
    }
}
