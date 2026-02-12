package co.com.bancolombia.usecase.getmaxstockproductsbyfranchise;

import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.franchise.FranchiseWithTopProducts;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class GetMaxStockProductsByFranchiseUseCase {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;

    public Mono<FranchiseWithTopProducts> execute(Long franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(
                        Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND)))
                )
                .flatMap(franchise ->
                        branchRepository.findBranchesWithTopProductByFranchiseId(franchiseId)
                                .collectList()
                                .map(branchesWithProducts -> FranchiseWithTopProducts.builder()
                                        .franchise(franchise)
                                        .branchesWithTopProducts(branchesWithProducts)
                                        .build())
                );
    }

}