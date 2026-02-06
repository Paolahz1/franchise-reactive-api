package co.com.bancolombia.model.franchise.gateways;

import co.com.bancolombia.model.franchise.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {

    Mono<Franchise> save(Franchise franchise);

    Mono<Franchise> findById(Long franchiseId);

    Mono<Void> updateName(Long franchiseId, String newName);
}
