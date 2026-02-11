package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.request.ProductRequest;
import co.com.bancolombia.api.dto.response.ProductResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
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
        loggingUtils.logRequest("ADD_PRODUCT_TO_BRANCH", request);
        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("branchId")))
                .flatMap(branchId -> 
                    request.bodyToMono(ProductRequest.class)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING))))
                        .map(productRequestMapper::toDomain)
                        .flatMap(product -> 
                            addProductToBranchUseCase.execute(branchId, product)
                        )
                )
                .map(productResponseMapper::toResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> removeProductFromBranch(ServerRequest request) {
        loggingUtils.logRequest("REMOVE_PRODUCT_FROM_BRANCH", request);
        return Mono.fromSupplier(() -> 
                    new Long[] {
                        Long.valueOf(request.pathVariable("branchId")),
                        Long.valueOf(request.pathVariable("productId"))
                    }
                )
                .flatMap(ids -> 
                    removeProductFromBranchUseCase.execute(ids[0], ids[1])
                        .doOnSuccess(v -> loggingUtils.logResponse("REMOVE_PRODUCT_FROM_BRANCH", HttpStatus.NO_CONTENT.value()))
                        .then(ServerResponse.noContent().build())
                )
                .doOnError(error -> loggingUtils.logError("REMOVE_PRODUCT_FROM_BRANCH", error));
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        loggingUtils.logRequest("UPDATE_PRODUCT_STOCK", request);
        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("productId")))
                .flatMap(productId ->
                    request.bodyToMono(UpdateStockRequest.class)
                        .doOnNext(req -> loggingUtils.logRequest("UPDATE_PRODUCT_STOCK", request, req))
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING))))
                        .flatMap(stockRequest ->
                            updateProductStockUseCase.execute(productId, stockRequest.getStock())
                        )
                )
                .map(productResponseMapper::toResponse)
                .doOnNext(response -> loggingUtils.logResponse("UPDATE_PRODUCT_STOCK", response, HttpStatus.OK.value()))
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .doOnError(error -> loggingUtils.logError("UPDATE_PRODUCT_STOCK", error));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        loggingUtils.logRequest("UPDATE_PRODUCT_NAME", request);
        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("productId")))
                .flatMap(productId ->
                    request.bodyToMono(UpdateNameRequest.class)
                        .doOnNext(req -> loggingUtils.logRequest("UPDATE_PRODUCT_NAME", request, req))
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING))))
                        .flatMap(updateRequest ->
                            updateProductNameUseCase.execute(productId, updateRequest.getName())
                        )
                )
                .map(productResponseMapper::toResponse)
                .doOnNext(response -> loggingUtils.logResponse("UPDATE_PRODUCT_NAME", response, HttpStatus.OK.value()))
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .doOnError(error -> loggingUtils.logError("UPDATE_PRODUCT_NAME", error));
    }
}
