package co.com.bancolombia.mysql.adapter;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.mysql.mapper.BranchMapper;
import co.com.bancolombia.mysql.repository.BranchR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchMySQLAdapter implements BranchRepository {

    private final BranchR2dbcRepository r2dbcRepository;
    private final BranchMapper branchMapper;

    @Override
    public Mono<Branch> findById(Long branchId) {
        return r2dbcRepository.findById(branchId)
            .map(branchMapper::toDomain);
    }

    @Override
    public Mono<Void> updateName(Long branchId, String newName) {
        return r2dbcRepository.updateName(branchId, newName)
            .then();
    }
}
