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
        return Mono.just(newName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NAME_EMPTY))))
                .flatMap(validName ->
                    productRepository.findById(productId)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND))))
                        .flatMap(product ->
                            productRepository.findByNameAndBranchId(validName.trim(), product.getBranchId())
                                .flatMap(existing ->
                                    existing.getId().equals(productId)
                                        ? productRepository.updateName(productId, validName.trim())
                                            .then(productRepository.findById(productId))
                                        : Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NAME_DUPLICATE)))
                                )
                                .switchIfEmpty(
                                    productRepository.updateName(productId, validName.trim())
                                        .then(productRepository.findById(productId))
                                )
                        )
                );
    }
}
