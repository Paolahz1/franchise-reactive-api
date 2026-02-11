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

    public Mono<Franchise> execute(Franchise franchise) {
        return franchiseRepository.findByName(franchise.getName())
                .flatMap( n ->
                    Mono.<Franchise>error(
                            new BusinessException(TechnicalMessage.FRANCHISE_NAME_ALREADY_EXISTS)
                    )
                )
                .switchIfEmpty(Mono.defer(() -> franchiseRepository.save(franchise)));
    }

}
