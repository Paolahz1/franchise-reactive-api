package co.com.bancolombia.api.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.bancolombia.api.dto.request.ProductRequest;
import co.com.bancolombia.api.dto.request.UpdateNameRequest;
import co.com.bancolombia.api.dto.request.UpdateStockRequest;
import co.com.bancolombia.api.mapper.ProductRequestMapper;
import co.com.bancolombia.api.mapper.ProductResponseMapper;
import co.com.bancolombia.api.utils.LoggingUtils;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.usecase.addproducttobranch.AddProductToBranchUseCase;
import co.com.bancolombia.usecase.removeproductfrombranch.RemoveProductFromBranchUseCase;
import co.com.bancolombia.usecase.updateproductname.UpdateProductNameUseCase;
import co.com.bancolombia.usecase.updateproductstock.UpdateProductStockUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final AddProductToBranchUseCase addProductToBranchUseCase;
    private final RemoveProductFromBranchUseCase removeProductFromBranchUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final ProductRequestMapper productRequestMapper;
    private final ProductResponseMapper productResponseMapper;
    private final LoggingUtils loggingUtils;

    public Mono<ServerResponse> addProductToBranch(ServerRequest request) {

        final String operation = "ADD_PRODUCT_TO_BRANCH";
        loggingUtils.logRequest(operation, request);

        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("branchId")))
                .flatMap(branchId ->
                        request.bodyToMono(ProductRequest.class)
                                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING)))
                                .map(productRequestMapper::toDomain)
                                .flatMap(product -> addProductToBranchUseCase.execute(branchId, product))
                )
                .map(productResponseMapper::toResponse)
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

    public Mono<ServerResponse> removeProductFromBranch(ServerRequest request) {

        final String operation = "REMOVE_PRODUCT_FROM_BRANCH";
        loggingUtils.logRequest(operation, request);

        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("branchId")))
                .zipWith(Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("productId"))))
                .flatMap(tuple ->
                        removeProductFromBranchUseCase.execute(tuple.getT1(), tuple.getT2())
                )
                .then(ServerResponse.noContent().build())
                .doOnSuccess(resp ->
                        loggingUtils.logResponse(operation, HttpStatus.NO_CONTENT.value())
                )
                .doOnError(error ->
                        loggingUtils.logError(operation, error)
                );
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {

        final String operation = "UPDATE_PRODUCT_STOCK";
        loggingUtils.logRequest(operation, request);

        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("productId")))
                .flatMap(productId ->
                        request.bodyToMono(UpdateStockRequest.class)
                                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING)))
                                .flatMap(stockRequest ->
                                        updateProductStockUseCase.execute(productId, stockRequest.getStock())
                                )
                )
                .map(productResponseMapper::toResponse)
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

    public Mono<ServerResponse> updateProductName(ServerRequest request) {

        final String operation = "UPDATE_PRODUCT_NAME";
        loggingUtils.logRequest(operation, request);

        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("productId")))
                .flatMap(productId ->
                        request.bodyToMono(UpdateNameRequest.class)
                                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING)))
                                .flatMap(updateRequest ->
                                        updateProductNameUseCase.execute(productId, updateRequest.getName())
                                )
                )
                .map(productResponseMapper::toResponse)
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
