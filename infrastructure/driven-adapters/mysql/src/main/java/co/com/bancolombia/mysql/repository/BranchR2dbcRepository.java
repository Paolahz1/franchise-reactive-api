package co.com.bancolombia.mysql.repository;

import co.com.bancolombia.mysql.entity.BranchEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

public interface BranchR2dbcRepository extends R2dbcRepository<BranchEntity, Long> {

    @Query("SELECT * FROM branches WHERE name = :name AND franchise_id = :franchiseId")
    Mono<BranchEntity> findByNameAndFranchiseId(String name, Long franchiseId);

    @Modifying
    @Query("UPDATE branches SET name = :name, updated_at = NOW() WHERE id = :id")
    Mono<Integer> updateName(@Param("id") Long id, @Param("name") String name);
}
