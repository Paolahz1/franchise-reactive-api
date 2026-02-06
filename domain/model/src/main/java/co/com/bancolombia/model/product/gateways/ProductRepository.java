package co.com.bancolombia.model.product.gateways;

import co.com.bancolombia.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {

    Mono<Product> save(Product product);

    Mono<Product> findById(Long productId);

    Mono<Product> findByNameAndBranchId(String name, Long branchId);

    Mono<Void> deleteById(Long productId);

    Mono<Void> updateStock(Long productId, Integer newStock);

    Mono<Void> updateName(Long productId, String newName);

    Flux<Product> findMaxStockByFranchise(Long franchiseId);
}
