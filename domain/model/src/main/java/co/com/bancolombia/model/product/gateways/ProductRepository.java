package co.com.bancolombia.model.product.gateways;

import co.com.bancolombia.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {

    Mono<Product> findById(Long productId);

    Mono<Void> save(Product product);

    Mono<Void> deleteById(Long productId);

    Mono<Void> updateStock(Long productId, Integer newStock);

    Flux<Product> findMaxStockByFranchise(Long franchiseId);
}
