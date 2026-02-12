package co.com.bancolombia.model.branch.gateways;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.BranchWithTopProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository {

    Mono<Branch> save(Branch branch);

    Mono<Branch> findById(Long branchId);

    Mono<Branch> findByNameAndFranchiseId(String name, Long franchiseId);

    Mono<Void> updateName(Long branchId, String newName);

    Flux<BranchWithTopProduct> findBranchesWithTopProductByFranchiseId(Long franchiseId);
}
