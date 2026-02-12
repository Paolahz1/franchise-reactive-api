package co.com.bancolombia.mysql.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import co.com.bancolombia.mysql.dto.BranchWithProductDto;
import co.com.bancolombia.mysql.entity.BranchEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchR2dbcRepository extends R2dbcRepository<BranchEntity, Long> {

    @Query("SELECT * FROM branches WHERE name = :name AND franchise_id = :franchiseId")
    Mono<BranchEntity> findByNameAndFranchiseId(String name, Long franchiseId);

    @Modifying
    @Query("UPDATE branches SET name = :name, updated_at = NOW() WHERE id = :id")
    Mono<Integer> updateName(@Param("id") Long id, @Param("name") String name);

    @Query("""
        SELECT b.id as branch_id, b.name as branch_name, b.franchise_id,
               p.id as product_id, p.name as product_name, p.stock as product_stock, p.branch_id as product_branch_id
        FROM branches b
        LEFT JOIN products p 
            ON p.branch_id = b.id
            AND p.stock = (
                SELECT MAX(p2.stock)
                FROM products p2
                WHERE p2.branch_id = b.id
            )
        WHERE b.franchise_id = :franchiseId
        """)
    Flux<BranchWithProductDto> findBranchesWithTopProductByFranchiseId(@Param("franchiseId") Long franchiseId);
}
