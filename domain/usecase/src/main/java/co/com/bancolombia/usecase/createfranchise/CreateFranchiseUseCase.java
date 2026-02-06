package co.com.bancolombia.usecase.createfranchise;

import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateFranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> execute(String name) {
        return Mono.justOrEmpty(name)
                .map(String::trim)
                .filter(n -> !n.isEmpty())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NAME_EMPTY))))
                .flatMap(trimmedName -> 
                    franchiseRepository.findByName(trimmedName)
                        .flatMap(existing -> Mono.defer(() -> 
                            Mono.<Franchise>error(new BusinessException(TechnicalMessage.FRANCHISE_NAME_ALREADY_EXISTS))))
                        .switchIfEmpty(Mono.defer(() -> {
                            Franchise newFranchise = Franchise.builder().name(trimmedName).build();
                            return franchiseRepository.save(newFranchise);
                        }))
                );
    }
}
