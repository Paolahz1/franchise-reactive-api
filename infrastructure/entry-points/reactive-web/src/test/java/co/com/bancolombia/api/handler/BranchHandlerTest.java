package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.request.BranchRequest;
import co.com.bancolombia.api.dto.request.UpdateNameRequest;
import co.com.bancolombia.api.dto.response.BranchResponse;
import co.com.bancolombia.api.mapper.BranchRequestMapper;
import co.com.bancolombia.api.mapper.BranchResponseMapper;
import co.com.bancolombia.api.utils.LoggingUtils;
import co.com.bancolombia.api.utils.ValidationUtils;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.usecase.addbranchtofranchise.AddBranchToFranchiseUseCase;
import co.com.bancolombia.usecase.updatebranchname.UpdateBranchNameUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BranchHandler - Unit Tests")
class BranchHandlerTest {

    @Mock
    private AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;

    @Mock
    private UpdateBranchNameUseCase updateBranchNameUseCase;

    @Mock
    private ValidationUtils validationUtils;

    @Mock
    private BranchRequestMapper branchRequestMapper;

    @Mock
    private BranchResponseMapper branchResponseMapper;

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private BranchHandler handler;

    @Test
    @DisplayName("Should add branch to franchise successfully")
    void shouldAddBranchToFranchiseSuccessfully() {
        // Arrange
        Long franchiseId = 1L;
        BranchRequest request = new BranchRequest();
        request.setName("Test Branch");

        Branch branch = Branch.builder()
                .name("Test Branch")
                .build();

        Branch savedBranch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(franchiseId)
                .build();

        BranchResponse response = new BranchResponse();
        response.setId(1L);
        response.setName("Test Branch");

        when(serverRequest.pathVariable("franchiseId")).thenReturn(String.valueOf(franchiseId));
        when(serverRequest.bodyToMono(BranchRequest.class)).thenReturn(Mono.just(request));
        when(validationUtils.validate(request)).thenReturn(Mono.just(request));
        when(branchRequestMapper.toDomain(request)).thenReturn(branch);
        when(addBranchToFranchiseUseCase.execute(franchiseId, branch)).thenReturn(Mono.just(savedBranch));
        when(branchResponseMapper.toResponse(savedBranch)).thenReturn(response);

        // Act & Assert
        StepVerifier.create(handler.addBranchToFranchise(serverRequest))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(loggingUtils).logRequest("ADD_BRANCH_TO_FRANCHISE", serverRequest);
        verify(addBranchToFranchiseUseCase).execute(franchiseId, branch);
    }

    @Test
    @DisplayName("Should update branch name successfully")
    void shouldUpdateBranchNameSuccessfully() {
        // Arrange
        Long branchId = 1L;
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Updated Branch");

        Branch updatedBranch = Branch.builder()
                .id(branchId)
                .name("Updated Branch")
                .franchiseId(1L)
                .build();

        BranchResponse response = new BranchResponse();
        response.setId(branchId);
        response.setName("Updated Branch");

        when(serverRequest.pathVariable("branchId")).thenReturn(String.valueOf(branchId));
        when(serverRequest.bodyToMono(UpdateNameRequest.class)).thenReturn(Mono.just(request));
        when(validationUtils.validate(request)).thenReturn(Mono.just(request));
        when(updateBranchNameUseCase.execute(branchId, "Updated Branch")).thenReturn(Mono.just(updatedBranch));
        when(branchResponseMapper.toResponse(updatedBranch)).thenReturn(response);

        // Act & Assert
        StepVerifier.create(handler.updateBranchName(serverRequest))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.OK
                )
                .verifyComplete();

        verify(loggingUtils).logRequest("UPDATE_BRANCH_NAME", serverRequest);
        verify(updateBranchNameUseCase).execute(branchId, "Updated Branch");
    }

    @Test
    @DisplayName("Should handle error when adding branch with empty body")
    void shouldHandleErrorWhenAddingBranchWithEmptyBody() {
        // Arrange
        Long franchiseId = 1L;
        when(serverRequest.pathVariable("franchiseId")).thenReturn(String.valueOf(franchiseId));
        when(serverRequest.bodyToMono(BranchRequest.class)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(handler.addBranchToFranchise(serverRequest))
                .expectError()
                .verify();

        verify(loggingUtils).logRequest("ADD_BRANCH_TO_FRANCHISE", serverRequest);
    }

    @Test
    @DisplayName("Should handle error when updating branch name with empty body")
    void shouldHandleErrorWhenUpdatingBranchNameWithEmptyBody() {
        // Arrange
        Long branchId = 1L;
        when(serverRequest.pathVariable("branchId")).thenReturn(String.valueOf(branchId));
        when(serverRequest.bodyToMono(UpdateNameRequest.class)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(handler.updateBranchName(serverRequest))
                .expectError()
                .verify();

        verify(loggingUtils).logRequest("UPDATE_BRANCH_NAME", serverRequest);
    }

    @Test
    @DisplayName("Should handle error when updating branch name fails")
    void shouldHandleErrorWhenUpdatingBranchNameFails() {
        // Arrange
        Long branchId = 1L;
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Updated Branch");
        RuntimeException error = new RuntimeException("Database error");

        when(serverRequest.pathVariable("branchId")).thenReturn(String.valueOf(branchId));
        when(serverRequest.bodyToMono(UpdateNameRequest.class)).thenReturn(Mono.just(request));
        when(validationUtils.validate(request)).thenReturn(Mono.just(request));
        when(updateBranchNameUseCase.execute(branchId, "Updated Branch")).thenReturn(Mono.error(error));

        // Act & Assert
        StepVerifier.create(handler.updateBranchName(serverRequest))
                .expectError(RuntimeException.class)
                .verify();

        verify(loggingUtils).logRequest("UPDATE_BRANCH_NAME", serverRequest);
        verify(loggingUtils).logError("UPDATE_BRANCH_NAME", error);
    }
}
