package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.BranchRequest;
import co.com.bancolombia.api.dto.BranchResponse;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.usecase.addbranchtofranchise.AddBranchToFranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchHandler {

    private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;

    public Mono<ServerResponse> addBranchToFranchise(ServerRequest request) {
        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("franchiseId")))
                .flatMap(franchiseId -> 
                    request.bodyToMono(BranchRequest.class)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING))))
                        .flatMap(branchRequest -> 
                            addBranchToFranchiseUseCase.execute(franchiseId, branchRequest.getName())
                        )
                )
                .flatMap(branch -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(BranchResponse.builder()
                                .id(branch.getId())
                                .name(branch.getName())
                                .franchiseId(branch.getFranchiseId())
                                .build()));
    }
}