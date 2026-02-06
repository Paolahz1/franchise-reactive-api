package co.com.bancolombia.mysql.repository;

import co.com.bancolombia.mysql.entity.ProductEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductR2dbcRepository extends R2dbcRepository<ProductEntity, Long> {

    @Modifying
    @Query("UPDATE products SET stock = :stock WHERE id = :id")
    Mono<Integer> updateStock(Long id, Integer stock);

    @Query("""
        SELECT p.* 
        FROM products p
        INNER JOIN branches b ON p.branch_id = b.id
        WHERE b.franchise_id = :franchiseId
        AND p.stock = (
            SELECT MAX(p2.stock) 
            FROM products p2 
            WHERE p2.branch_id = p.branch_id
        )
        ORDER BY b.id, p.stock DESC
        """)
    Flux<ProductEntity> findMaxStockByFranchise(Long franchiseId);
}
