package co.com.bancolombia.mysql.repository;

import co.com.bancolombia.mysql.entity.FranchiseEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FranchiseR2dbcRepository extends R2dbcRepository <FranchiseEntity, Long>{
}
