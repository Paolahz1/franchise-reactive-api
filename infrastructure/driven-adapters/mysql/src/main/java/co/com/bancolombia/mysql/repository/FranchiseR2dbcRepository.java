package co.com.bancolombia.mysql.repository;

import co.com.bancolombia.mysql.entity.FranchiseEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface FranchiseR2dbcRepository extends R2dbcRepository<FranchiseEntity, Long> {

    @Query("SELECT * FROM franchises WHERE name = :name")
    Mono<FranchiseEntity> findByName(String name);

    @Modifying
    @Query("UPDATE franchises SET name = :name WHERE id = :id")
    Mono<Integer> updateName(Long id, String name);
}
