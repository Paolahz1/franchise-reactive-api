package co.com.bancolombia.usecase.updateproductstock;

import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductStockUseCase {

    private final ProductRepository productRepository;

    public Mono<Product> execute(Long productId, Integer newStock) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND))))
                .flatMap(product ->
                    productRepository.updateStock(productId, newStock)
                        .then(Mono.just(product.toBuilder().stock(newStock).build()))
                );
    }
}
