package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.response.BranchWithTopProductResponse;
import co.com.bancolombia.api.dto.request.FranchiseRequest;
import co.com.bancolombia.api.dto.response.FranchiseResponse;
import co.com.bancolombia.api.dto.response.FranchiseWithMaxStockProductsResponse;
import co.com.bancolombia.api.dto.response.TopProductResponse;
import co.com.bancolombia.api.dto.request.UpdateNameRequest;
import co.com.bancolombia.api.mapper.FranchiseRequestMapper;
import co.com.bancolombia.api.mapper.FranchiseResponseMapper;
import co.com.bancolombia.api.utils.LoggingUtils;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.getmaxstockproductsbyfranchise.GetMaxStockProductsByFranchiseUseCase;
import co.com.bancolombia.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final GetMaxStockProductsByFranchiseUseCase getMaxStockProductsByFranchiseUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final FranchiseRequestMapper franchiseRequestMapper;
    private final FranchiseResponseMapper franchiseResponseMapper;
    private final LoggingUtils loggingUtils;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        loggingUtils.logRequest("CREATE_FRANCHISE", request);
        return request.bodyToMono(FranchiseRequest.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING))))
                .map(franchiseRequestMapper::toDomain)
                .flatMap(createFranchiseUseCase::execute)
                .map(franchiseResponseMapper::toResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
        loggingUtils.logRequest("GET_MAX_STOCK_PRODUCTS", request);
        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("franchiseId")))
                .flatMap(franchiseId ->
                    getMaxStockProductsByFranchiseUseCase.execute(franchiseId)
                        .map(result -> FranchiseWithMaxStockProductsResponse.builder()
                                .franchiseId(result.getFranchise().getId())
                                .franchiseName(result.getFranchise().getName())
                                .branches(
                                    result.getBranchesWithTopProducts().stream()
                                        .map(branchWithProduct -> BranchWithTopProductResponse.builder()
                                                .branchId(branchWithProduct.getBranch().getId())
                                                .branchName(branchWithProduct.getBranch().getName())
                                                .topProduct(TopProductResponse.builder()
                                                        .productId(branchWithProduct.getTopProduct().getId())
                                                        .productName(branchWithProduct.getTopProduct().getName())
                                                        .stock(branchWithProduct.getTopProduct().getStock())
                                                        .build())
                                                .build())
                                        .collect(Collectors.toList())
                                )
                                .build())
                )
                .doOnNext(response -> loggingUtils.logResponse("GET_MAX_STOCK_PRODUCTS", response, HttpStatus.OK.value()))
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .doOnError(error -> loggingUtils.logError("GET_MAX_STOCK_PRODUCTS", error));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        loggingUtils.logRequest("UPDATE_FRANCHISE_NAME", request);
        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("franchiseId")))
                .flatMap(franchiseId ->
                    request.bodyToMono(UpdateNameRequest.class)
                        .doOnNext(req -> loggingUtils.logRequest("UPDATE_FRANCHISE_NAME", request, req))
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING))))
                        .flatMap(updateRequest ->
                            updateFranchiseNameUseCase.execute(franchiseId, updateRequest.getName())
                        )
                )
                .map(franchiseResponseMapper::toResponse)
                .doOnNext(response -> loggingUtils.logResponse("UPDATE_FRANCHISE_NAME", response, HttpStatus.OK.value()))
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .doOnError(error -> loggingUtils.logError("UPDATE_FRANCHISE_NAME", error));
    }
}
