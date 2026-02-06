package co.com.bancolombia.mysql.adapter;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.mysql.repository.FranchiseR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseMySQLAdapter implements FranchiseRepository {

    private final FranchiseR2dbcRepository r2dbcRepository;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return null;
    }

    @Override
    public Mono<Franchise> findById(Long franchiseId) {
        return null;
    }

    @Override
    public Mono<Void> updateName(Long franchiseId, String newName) {
        return null;
    }
}
