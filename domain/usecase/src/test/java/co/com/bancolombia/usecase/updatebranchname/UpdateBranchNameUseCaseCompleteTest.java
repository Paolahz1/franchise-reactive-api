package co.com.bancolombia.usecase.updatebranchname;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateBranchNameUseCase - Complete Coverage Tests")
class UpdateBranchNameUseCaseCompleteTest {

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private UpdateBranchNameUseCase useCase;

    @Test
    @DisplayName("Should update branch name successfully when new name is unique")
    void shouldUpdateBranchNameSuccessfully_WhenNewNameIsUnique() {
        // Arrange
        Long branchId = 1L;
        Long franchiseId = 10L;
        String newName = "Updated Branch";

        Branch existingBranch = Branch.builder()
                .id(branchId)
                .name("Old Branch")
                .franchiseId(franchiseId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(branchRepository.findByNameAndFranchiseId(newName, franchiseId)).thenReturn(Mono.empty());
        when(branchRepository.updateName(branchId, newName)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, newName))
                .expectNext(existingBranch)
                .verifyComplete();

        verify(branchRepository, times(1)).findById(branchId);
        verify(branchRepository, times(1)).findByNameAndFranchiseId(newName, franchiseId);
        verify(branchRepository, times(1)).updateName(branchId, newName);
    }

    @Test
    @DisplayName("Should update branch name successfully when updating to same name (same ID)")
    void shouldUpdateBranchNameSuccessfully_WhenUpdatingToSameName() {
        // Arrange
        Long branchId = 1L;
        Long franchiseId = 10L;
        String sameName = "Same Branch";

        Branch existingBranch = Branch.builder()
                .id(branchId)
                .name(sameName)
                .franchiseId(franchiseId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(branchRepository.findByNameAndFranchiseId(sameName, franchiseId))
                .thenReturn(Mono.just(existingBranch));
        when(branchRepository.updateName(branchId, sameName)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, sameName))
                .expectNext(existingBranch)
                .verifyComplete();

        verify(branchRepository, times(1)).findById(branchId);
        verify(branchRepository, times(1)).findByNameAndFranchiseId(sameName, franchiseId);
        verify(branchRepository, times(1)).updateName(branchId, sameName);
    }

    @Test
    @DisplayName("Should throw BusinessException when branch does not exist")
    void shouldThrowBusinessException_WhenBranchDoesNotExist() {
        // Arrange
        Long branchId = 999L;
        String newName = "New Name";

        when(branchRepository.findById(branchId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, newName))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.BRANCH_NOT_FOUND
                )
                .verify();

        verify(branchRepository, times(1)).findById(branchId);
        verify(branchRepository, never()).findByNameAndFranchiseId(anyString(), anyLong());
        verify(branchRepository, never()).updateName(anyLong(), anyString());
    }

    @Test
    @DisplayName("Should throw BusinessException when new name already exists in franchise (different ID)")
    void shouldThrowBusinessException_WhenNewNameAlreadyExistsInFranchise() {
        // Arrange
        Long branchId = 1L;
        Long franchiseId = 10L;
        String newName = "Existing Branch";

        Branch currentBranch = Branch.builder()
                .id(branchId)
                .name("Old Branch")
                .franchiseId(franchiseId)
                .build();

        Branch otherBranch = Branch.builder()
                .id(2L)  // Different ID
                .name(newName)
                .franchiseId(franchiseId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(currentBranch));
        when(branchRepository.findByNameAndFranchiseId(newName, franchiseId))
                .thenReturn(Mono.just(otherBranch));

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, newName))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.BRANCH_NAME_DUPLICATE
                )
                .verify();

        verify(branchRepository, times(1)).findById(branchId);
        verify(branchRepository, times(1)).findByNameAndFranchiseId(newName, franchiseId);
        verify(branchRepository, never()).updateName(anyLong(), anyString());
    }

    @Test
    @DisplayName("Should handle whitespace in new name")
    void shouldHandleWhitespaceInNewName() {
        // Arrange
        Long branchId = 1L;
        Long franchiseId = 10L;
        String newNameWithSpaces = "  Updated Branch  ";
        String trimmedName = "Updated Branch";

        Branch existingBranch = Branch.builder()
                .id(branchId)
                .name("Old Branch")
                .franchiseId(franchiseId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(branchRepository.findByNameAndFranchiseId(trimmedName, franchiseId)).thenReturn(Mono.empty());
        when(branchRepository.updateName(branchId, trimmedName)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, newNameWithSpaces))
                .expectNext(existingBranch)
                .verifyComplete();

        verify(branchRepository, times(1)).findById(branchId);
        verify(branchRepository, times(1)).findByNameAndFranchiseId(trimmedName, franchiseId);
        verify(branchRepository, times(1)).updateName(branchId, trimmedName);
    }
}
