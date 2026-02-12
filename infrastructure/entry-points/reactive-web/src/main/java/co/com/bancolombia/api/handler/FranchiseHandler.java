package co.com.bancolombia.api.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.bancolombia.api.dto.request.FranchiseRequest;
import co.com.bancolombia.api.dto.request.UpdateNameRequest;
import co.com.bancolombia.api.mapper.FranchiseRequestMapper;
import co.com.bancolombia.api.mapper.FranchiseResponseMapper;
import co.com.bancolombia.api.mapper.FranchiseWithMaxStockProductsResponseMapper;
import co.com.bancolombia.api.utils.LoggingUtils;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.getmaxstockproductsbyfranchise.GetMaxStockProductsByFranchiseUseCase;
import co.com.bancolombia.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final GetMaxStockProductsByFranchiseUseCase getMaxStockProductsByFranchiseUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final FranchiseRequestMapper franchiseRequestMapper;
    private final FranchiseResponseMapper franchiseResponseMapper;
    private final FranchiseWithMaxStockProductsResponseMapper franchiseWithMaxStockProductsResponseMapper;
    private final LoggingUtils loggingUtils;

public Mono<ServerResponse> createFranchise(ServerRequest request) {

        final String operation = "CREATE_FRANCHISE";
        loggingUtils.logRequest(operation, request);

        return request.bodyToMono(FranchiseRequest.class)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING)))
                .map(franchiseRequestMapper::toDomain)
                .flatMap(createFranchiseUseCase::execute)
                .map(franchiseResponseMapper::toResponse)
                .flatMap(response ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)
                )
                .doOnSuccess(resp ->
                        loggingUtils.logResponse(operation, HttpStatus.CREATED.value())
                )
                .doOnError(error ->
                        loggingUtils.logError(operation, error)
                );
    }

        public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {

                final String operation = "GET_MAX_STOCK_PRODUCTS";
                loggingUtils.logRequest(operation, request);

                return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("franchiseId")))
                        .flatMap(getMaxStockProductsByFranchiseUseCase::execute)
                        .map(franchiseWithMaxStockProductsResponseMapper::toResponse)
                        .flatMap(response ->
                                ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(response)
                        )
                        .doOnSuccess(resp ->
                                loggingUtils.logResponse(operation, HttpStatus.OK.value())
                        )
                        .doOnError(error ->
                                loggingUtils.logError(operation, error)
                        );
        }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {

        final String operation = "UPDATE_FRANCHISE_NAME";
        loggingUtils.logRequest(operation, request);

        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("franchiseId")))
                .flatMap(franchiseId ->
                        request.bodyToMono(UpdateNameRequest.class)
                                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING)))
                                .flatMap(updateRequest ->
                                        updateFranchiseNameUseCase.execute(franchiseId, updateRequest.getName())
                                )
                )
                .map(franchiseResponseMapper::toResponse)
                .flatMap(response ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)
                )
                .doOnSuccess(resp ->
                        loggingUtils.logResponse(operation, HttpStatus.OK.value())
                )
                .doOnError(error ->
                        loggingUtils.logError(operation, error)
                );
    }
}
