package co.com.bancolombia.usecase.addbranchtofranchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddBranchToFranchiseUseCase - Complete Coverage Tests")
class AddBranchToFranchiseUseCaseCompleteTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private AddBranchToFranchiseUseCase useCase;

    @Test
    @DisplayName("Should add branch successfully when franchise exists and branch name is unique")
    void shouldAddBranchSuccessfully_WhenFranchiseExistsAndBranchNameIsUnique() {
        // Arrange
        Long franchiseId = 1L;
        Branch inputBranch = Branch.builder()
                .name("New Branch")
                .build();

        Franchise franchise = Franchise.builder()
                .id(franchiseId)
                .name("Test Franchise")
                .build();

        Branch savedBranch = Branch.builder()
                .id(10L)
                .name("New Branch")
                .franchiseId(franchiseId)
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchRepository.findByNameAndFranchiseId(anyString(), anyLong())).thenReturn(Mono.empty());
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(savedBranch));

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId, inputBranch))
                .expectNext(savedBranch)
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(branchRepository, times(1)).findByNameAndFranchiseId("New Branch", franchiseId);
        verify(branchRepository, times(1)).save(any(Branch.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when franchise does not exist")
    void shouldThrowBusinessException_WhenFranchiseDoesNotExist() {
        // Arrange
        Long franchiseId = 999L;
        Branch inputBranch = Branch.builder()
                .name("New Branch")
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId, inputBranch))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.FRANCHISE_NOT_FOUND
                )
                .verify();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(branchRepository, never()).findByNameAndFranchiseId(anyString(), anyLong());
        verify(branchRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when branch name already exists in franchise")
    void shouldThrowBusinessException_WhenBranchNameAlreadyExistsInFranchise() {
        // Arrange
        Long franchiseId = 1L;
        Branch inputBranch = Branch.builder()
                .name("Existing Branch")
                .build();

        Franchise franchise = Franchise.builder()
                .id(franchiseId)
                .name("Test Franchise")
                .build();

        Branch existingBranch = Branch.builder()
                .id(5L)
                .name("Existing Branch")
                .franchiseId(franchiseId)
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchRepository.findByNameAndFranchiseId(anyString(), anyLong()))
                .thenReturn(Mono.just(existingBranch));

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId, inputBranch))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.BRANCH_NAME_ALREADY_EXISTS
                )
                .verify();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(branchRepository, times(1)).findByNameAndFranchiseId("Existing Branch", franchiseId);
        verify(branchRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate error when franchise repository fails")
    void shouldPropagateError_WhenFranchiseRepositoryFails() {
        // Arrange
        Long franchiseId = 1L;
        Branch inputBranch = Branch.builder()
                .name("New Branch")
                .build();

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId, inputBranch))
                .expectError(RuntimeException.class)
                .verify();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(branchRepository, never()).findByNameAndFranchiseId(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should propagate error when branch save fails")
    void shouldPropagateError_WhenBranchSaveFails() {
        // Arrange
        Long franchiseId = 1L;
        Branch inputBranch = Branch.builder()
                .name("New Branch")
                .build();

        Franchise franchise = Franchise.builder()
                .id(franchiseId)
                .name("Test Franchise")
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchRepository.findByNameAndFranchiseId(anyString(), anyLong())).thenReturn(Mono.empty());
        when(branchRepository.save(any(Branch.class)))
                .thenReturn(Mono.error(new RuntimeException("Save failed")));

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId, inputBranch))
                .expectError(RuntimeException.class)
                .verify();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(branchRepository, times(1)).findByNameAndFranchiseId("New Branch", franchiseId);
        verify(branchRepository, times(1)).save(any(Branch.class));
    }
}
