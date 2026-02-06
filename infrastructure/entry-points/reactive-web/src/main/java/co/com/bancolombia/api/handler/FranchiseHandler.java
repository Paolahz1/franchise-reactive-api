package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.FranchiseRequest;
import co.com.bancolombia.api.dto.FranchiseResponse;
import co.com.bancolombia.api.dto.ProductWithBranchResponse;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.getmaxstockproductsbyfranchise.GetMaxStockProductsByFranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final GetMaxStockProductsByFranchiseUseCase getMaxStockProductsByFranchiseUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING))))
                .flatMap(franchiseRequest -> createFranchiseUseCase.execute(franchiseRequest.getName()))
                .flatMap(franchise -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(FranchiseResponse.builder()
                                .id(franchise.getId())
                                .name(franchise.getName())
                                .build()));
    }

    public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("franchiseId")))
                .flatMapMany(franchiseId ->
                    getMaxStockProductsByFranchiseUseCase.execute(franchiseId)
                        .map(productWithBranch ->
                            ProductWithBranchResponse.builder()
                                    .productId(productWithBranch.getProduct().getId())
                                    .productName(productWithBranch.getProduct().getName())
                                    .stock(productWithBranch.getProduct().getStock())
                                    .branchId(productWithBranch.getBranch().getId())
                                    .branchName(productWithBranch.getBranch().getName())
                                    .build()
                        )
                )
                .collectList()
                .flatMap(products -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(products));
    }
}
