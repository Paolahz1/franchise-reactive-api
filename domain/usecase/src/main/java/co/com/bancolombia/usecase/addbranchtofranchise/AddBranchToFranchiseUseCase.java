package co.com.bancolombia.usecase.addbranchtofranchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddBranchToFranchiseUseCase {

    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    public Mono<Branch> execute(Long franchiseId, Branch branch) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND))))
                .flatMap(franchise -> {
                            return branchRepository.findByNameAndFranchiseId(branch.getName(), franchiseId)
                                    .flatMap(existing ->
                                            Mono.<Branch>error(new BusinessException(TechnicalMessage.BRANCH_NAME_ALREADY_EXISTS))
                                    )
                                    .switchIfEmpty(Mono.defer(() -> {
                                        branch.setFranchiseId(franchiseId);
                                        return branchRepository.save(branch);
                                    }));
                        }
                );
    }
}
