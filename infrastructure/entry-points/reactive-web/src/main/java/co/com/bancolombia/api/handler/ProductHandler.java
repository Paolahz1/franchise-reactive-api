package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.ProductRequest;
import co.com.bancolombia.api.dto.ProductResponse;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.usecase.addproducttobranch.AddProductToBranchUseCase;
import co.com.bancolombia.usecase.removeproductfrombranch.RemoveProductFromBranchUseCase;
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
}
