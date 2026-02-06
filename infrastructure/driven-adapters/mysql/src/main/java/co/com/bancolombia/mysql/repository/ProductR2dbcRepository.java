package co.com.bancolombia.mysql.repository;

import co.com.bancolombia.mysql.entity.ProductEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductR2dbcRepository extends R2dbcRepository<ProductEntity, Long> {

    @Query("SELECT * FROM products WHERE name = :name AND branch_id = :branchId")
    Mono<ProductEntity> findByNameAndBranchId(String name, Long branchId);

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
            INNER JOIN branches b2 ON p2.branch_id = b2.id
            WHERE b2.franchise_id = :franchiseId
        )
        ORDER BY p.stock DESC, p.name
        """)
    Flux<ProductEntity> findMaxStockByFranchise(Long franchiseId);
}
