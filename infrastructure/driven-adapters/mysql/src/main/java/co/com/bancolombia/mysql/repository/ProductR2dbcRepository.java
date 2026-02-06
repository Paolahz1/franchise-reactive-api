package co.com.bancolombia.mysql.repository;

import co.com.bancolombia.mysql.entity.ProductEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductR2dbcRepository extends R2dbcRepository<ProductEntity, Long> {

    @Query("SELECT * FROM products WHERE name = :name AND branch_id = :branchId")
    Mono<ProductEntity> findByNameAndBranchId(String name, Long branchId);

    @Modifying
    @Query("UPDATE products SET stock = :stock, updated_at = NOW() WHERE id = :id")
    Mono<Integer> updateStock(@Param("id") Long id, @Param("stock") Integer stock);

    @Modifying
    @Query("UPDATE products SET name = :name, updated_at = NOW() WHERE id = :id")
    Mono<Integer> updateName(@Param("id") Long id, @Param("name") String name);

    @Query("""
        SELECT p.* 
        FROM products p
        INNER JOIN branches b ON p.branch_id = b.id
        INNER JOIN (
            SELECT branch_id, MAX(stock) as max_stock
            FROM products
            WHERE branch_id IN (
                SELECT id FROM branches WHERE franchise_id = :franchiseId
            )
            GROUP BY branch_id
        ) max_products ON p.branch_id = max_products.branch_id AND p.stock = max_products.max_stock
        WHERE b.franchise_id = :franchiseId
        ORDER BY b.name, p.name
        """)
    Flux<ProductEntity> findMaxStockByFranchise(Long franchiseId);
}
