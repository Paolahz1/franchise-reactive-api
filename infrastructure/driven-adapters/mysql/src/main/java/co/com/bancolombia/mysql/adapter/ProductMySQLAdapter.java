package co.com.bancolombia.mysql.adapter;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import co.com.bancolombia.mysql.mapper.ProductMapper;
import co.com.bancolombia.mysql.repository.ProductR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductMySQLAdapter implements ProductRepository {

    private final ProductR2dbcRepository r2dbcRepository;
    private final ProductMapper productMapper;

    @Override
    public Mono<Product> save(Product product) {
        return Mono.fromSupplier(() -> productMapper.toEntity(product))
            .flatMap(r2dbcRepository::save)
            .map(productMapper::toDomain);
    }

    @Override
    public Mono<Product> findById(Long productId) {
        return r2dbcRepository.findById(productId)
            .map(productMapper::toDomain);
    }

    @Override
    public Mono<Product> findByNameAndBranchId(String name, Long branchId) {
        return r2dbcRepository.findByNameAndBranchId(name, branchId)
            .map(productMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long productId) {
        return r2dbcRepository.deleteById(productId);
    }

    @Override
    public Mono<Void> updateStock(Long productId, Integer newStock) {
        return r2dbcRepository.updateStock(productId, newStock)
            .then();
    }

    @Override
    public Flux<Product> findMaxStockByFranchise(Long franchiseId) {
        return r2dbcRepository.findMaxStockByFranchise(franchiseId)
            .map(productMapper::toDomain);
    }
}
