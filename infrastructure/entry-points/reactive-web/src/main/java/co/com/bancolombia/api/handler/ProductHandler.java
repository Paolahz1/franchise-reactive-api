package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.ProductRequest;
import co.com.bancolombia.api.dto.ProductResponse;
import co.com.bancolombia.api.dto.UpdateNameRequest;
import co.com.bancolombia.api.dto.UpdateStockRequest;
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

    public Mono<ServerResponse> addProductToBranch(ServerRequest request) {
        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("branchId")))
                .flatMap(branchId -> 
                    request.bodyToMono(ProductRequest.class)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING))))
                        .flatMap(productRequest -> 
                            addProductToBranchUseCase.execute(branchId, productRequest.getName(), productRequest.getStock())
                        )
                )
                .flatMap(product -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ProductResponse.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .stock(product.getStock())
                                .branchId(product.getBranchId())
                                .build()));
    }

    public Mono<ServerResponse> removeProductFromBranch(ServerRequest request) {
        return Mono.fromSupplier(() -> 
                    new Long[] {
                        Long.valueOf(request.pathVariable("branchId")),
                        Long.valueOf(request.pathVariable("productId"))
                    }
                )
                .flatMap(ids -> 
                    removeProductFromBranchUseCase.execute(ids[0], ids[1])
                        .then(ServerResponse.noContent().build())
                );
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("productId")))
                .flatMap(productId ->
                    request.bodyToMono(UpdateStockRequest.class)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING))))
                        .flatMap(stockRequest ->
                            updateProductStockUseCase.execute(productId, stockRequest.getStock())
                        )
                )
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ProductResponse.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .stock(product.getStock())
                                .branchId(product.getBranchId())
                                .build()));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("productId")))
                .flatMap(productId ->
                    request.bodyToMono(UpdateNameRequest.class)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING))))
                        .flatMap(updateRequest ->
                            updateProductNameUseCase.execute(productId, updateRequest.getName())
                        )
                )
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ProductResponse.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .stock(product.getStock())
                                .branchId(product.getBranchId())
                                .build()));
    }
}
