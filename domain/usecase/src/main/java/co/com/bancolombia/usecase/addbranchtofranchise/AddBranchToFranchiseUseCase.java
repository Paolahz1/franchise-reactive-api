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

    public Mono<Branch> execute(Long franchiseId, String branchName) {
        return Mono.justOrEmpty(branchName)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.BRANCH_NAME_EMPTY))))
                .flatMap(trimmedName -> 
                    franchiseRepository.findById(franchiseId)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND))))
                        .flatMap(franchise -> 
                            branchRepository.findByNameAndFranchiseId(trimmedName, franchiseId)
                                .flatMap(existing -> Mono.defer(() -> 
                                    Mono.<Branch>error(new BusinessException(TechnicalMessage.BRANCH_NAME_ALREADY_EXISTS))))
                                .switchIfEmpty(Mono.defer(() -> {
                                    Branch newBranch = Branch.builder()
                                            .name(trimmedName)
                                            .franchiseId(franchiseId)
                                            .build();
                                    return branchRepository.save(newBranch);
                                }))
                        )
                );
    }
}
