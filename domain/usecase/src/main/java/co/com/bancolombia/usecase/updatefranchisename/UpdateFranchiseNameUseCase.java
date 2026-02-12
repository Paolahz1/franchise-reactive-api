package co.com.bancolombia.usecase.updatefranchisename;

import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateFranchiseNameUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> execute(Long franchiseId, String newName) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND))))
                .flatMap(franchise ->
                    franchiseRepository.findByName(newName.trim())
                        .flatMap(existing -> {
                            if (existing.getId().equals(franchiseId)) {
                                return franchiseRepository.updateName(franchiseId, newName.trim())
                                        .then(Mono.just(franchise));
                            } else {
                                return Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NAME_DUPLICATE));
                            }
                        })
                        .switchIfEmpty(Mono.defer(() ->
                            franchiseRepository.updateName(franchiseId, newName.trim())
                                .then(Mono.just(franchise))
                        ))
                );
    }
}
