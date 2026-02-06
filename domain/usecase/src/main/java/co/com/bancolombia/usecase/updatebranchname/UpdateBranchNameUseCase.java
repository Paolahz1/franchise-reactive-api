package co.com.bancolombia.usecase.updatebranchname;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateBranchNameUseCase {

    private final BranchRepository branchRepository;

    public Mono<Branch> execute(Long branchId, String newName) {
        return Mono.just(newName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.BRANCH_NAME_EMPTY))))
                .flatMap(validName ->
                    branchRepository.findById(branchId)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND))))
                        .flatMap(branch ->
                            branchRepository.findByNameAndFranchiseId(validName.trim(), branch.getFranchiseId())
                                .flatMap(existing ->
                                    existing.getId().equals(branchId)
                                        ? branchRepository.updateName(branchId, validName.trim())
                                            .then(branchRepository.findById(branchId))
                                        : Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.BRANCH_NAME_DUPLICATE)))
                                )
                                .switchIfEmpty(
                                    branchRepository.updateName(branchId, validName.trim())
                                        .then(branchRepository.findById(branchId))
                                )
                        )
                );
    }
}
