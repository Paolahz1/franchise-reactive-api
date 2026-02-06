package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.ProductRequest;
import co.com.bancolombia.api.dto.ProductResponse;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.usecase.addproducttobranch.AddProductToBranchUseCase;
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

    public Mono<ServerResponse> addProductToBranch(ServerRequest request) {
        String branchIdParam = request.pathVariable("branchId");
        
        return request.bodyToMono(ProductRequest.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING))))
                .flatMap(productRequest -> {
                    Long branchId = Long.valueOf(branchIdParam);
                    return addProductToBranchUseCase.execute(branchId, productRequest.getName(), productRequest.getStock());
                })
                .flatMap(product -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ProductResponse.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .stock(product.getStock())
                                .branchId(product.getBranchId())
                                .build()));
    }
}
