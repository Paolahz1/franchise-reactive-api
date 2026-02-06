package co.com.bancolombia.mysql.adapter;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.mysql.mapper.FranchiseMapper;
import co.com.bancolombia.mysql.repository.FranchiseR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Component
@RequiredArgsConstructor
public class FranchiseMySQLAdapter implements FranchiseRepository {

    private final FranchiseR2dbcRepository r2dbcRepository;
    private final FranchiseMapper franchiseMapper;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return Mono.fromSupplier(() -> franchiseMapper.toEntity(franchise))
            .flatMap(r2dbcRepository::save)
            .map(franchiseMapper::toDomain);
    }

    @Override
    public Mono<Franchise> findById(Long franchiseId) {
        return r2dbcRepository.findById(franchiseId)
            .map(franchiseMapper::toDomain);
    }

    @Override
    public Mono<Franchise> findByName(String name) {
        return r2dbcRepository.findByName(name)
            .map(franchiseMapper::toDomain);
    }

    @Override
    public Mono<Void> updateName(Long franchiseId, String newName) {
        return r2dbcRepository.updateName(franchiseId, newName)
            .then();
    }
}
