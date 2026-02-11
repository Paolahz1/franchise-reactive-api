package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.request.BranchRequest;
import co.com.bancolombia.api.dto.response.BranchResponse;
import co.com.bancolombia.api.dto.request.UpdateNameRequest;
import co.com.bancolombia.api.mapper.BranchRequestMapper;
import co.com.bancolombia.api.mapper.BranchResponseMapper;
import co.com.bancolombia.api.utils.LoggingUtils;
import co.com.bancolombia.api.utils.ValidationUtils;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.usecase.addbranchtofranchise.AddBranchToFranchiseUseCase;
import co.com.bancolombia.usecase.updatebranchname.UpdateBranchNameUseCase;
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
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final ValidationUtils validationUtils;
    private final BranchRequestMapper branchRequestMapper;
    private final BranchResponseMapper branchResponseMapper;
    private final LoggingUtils loggingUtils;

    public Mono<ServerResponse> addBranchToFranchise(ServerRequest request) {
        loggingUtils.logRequest("ADD_BRANCH_TO_FRANCHISE", request);
        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("franchiseId")))
                .flatMap(franchiseId ->
                    request.bodyToMono(BranchRequest.class)
                        .switchIfEmpty(Mono.defer(
                                () -> Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING)))
                        )
                        .flatMap(validationUtils::validate)
                        .map(branchRequestMapper::toDomain)
                        .flatMap(branch -> addBranchToFranchiseUseCase.execute(franchiseId, branch))
                )
                .map(branchResponseMapper::toResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        loggingUtils.logRequest("UPDATE_BRANCH_NAME", request);
        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable("branchId")))
                .flatMap(branchId ->
                    request.bodyToMono(UpdateNameRequest.class)
                        .doOnNext(req -> loggingUtils.logRequest("UPDATE_BRANCH_NAME", request, req))
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.REQUIRED_FIELD_MISSING))))
                        .flatMap(validationUtils::validate)
                        .flatMap(updateRequest ->
                            updateBranchNameUseCase.execute(branchId, updateRequest.getName())
                        )
                )
                .map(branchResponseMapper::toResponse)
                .doOnNext(response -> loggingUtils.logResponse("UPDATE_BRANCH_NAME", response, HttpStatus.OK.value()))
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .doOnError(error -> loggingUtils.logError("UPDATE_BRANCH_NAME", error));
    }
}