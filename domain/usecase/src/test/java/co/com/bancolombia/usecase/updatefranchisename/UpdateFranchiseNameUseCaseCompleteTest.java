package co.com.bancolombia.usecase.updatefranchisename;

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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateFranchiseNameUseCase - Complete Coverage Tests")
class UpdateFranchiseNameUseCaseCompleteTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private UpdateFranchiseNameUseCase useCase;

    @Test
    @DisplayName("Should update franchise name successfully when new name is unique")
    void shouldUpdateFranchiseNameSuccessfully_WhenNewNameIsUnique() {
        // Arrange
        Long franchiseId = 1L;
        String newName = "Updated Franchise";

        Franchise existingFranchise = Franchise.builder()
                .id(franchiseId)
                .name("Old Franchise")
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepository.findByName(newName)).thenReturn(Mono.empty());
        when(franchiseRepository.updateName(franchiseId, newName)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId, newName))
                .expectNext(existingFranchise)
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(franchiseRepository, times(1)).findByName(newName);
        verify(franchiseRepository, times(1)).updateName(franchiseId, newName);
    }

    @Test
    @DisplayName("Should update franchise name successfully when updating to same name (same ID)")
    void shouldUpdateFranchiseNameSuccessfully_WhenUpdatingToSameName() {
        // Arrange
        Long franchiseId = 1L;
        String sameName = "Same Franchise";

        Franchise existingFranchise = Franchise.builder()
                .id(franchiseId)
                .name(sameName)
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepository.findByName(sameName)).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepository.updateName(franchiseId, sameName)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId, sameName))
                .expectNext(existingFranchise)
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(franchiseRepository, times(1)).findByName(sameName);
        verify(franchiseRepository, times(1)).updateName(franchiseId, sameName);
    }

    @Test
    @DisplayName("Should throw BusinessException when franchise does not exist")
    void shouldThrowBusinessException_WhenFranchiseDoesNotExist() {
        // Arrange
        Long franchiseId = 999L;
        String newName = "New Name";

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId, newName))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.FRANCHISE_NOT_FOUND
                )
                .verify();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(franchiseRepository, never()).findByName(anyString());
        verify(franchiseRepository, never()).updateName(anyLong(), anyString());
    }

    @Test
    @DisplayName("Should throw BusinessException when new name already exists (different ID)")
    void shouldThrowBusinessException_WhenNewNameAlreadyExistsDifferentId() {
        // Arrange
        Long franchiseId = 1L;
        String newName = "Existing Franchise";

        Franchise currentFranchise = Franchise.builder()
                .id(franchiseId)
                .name("Old Franchise")
                .build();

        Franchise otherFranchise = Franchise.builder()
                .id(2L)  // Different ID
                .name(newName)
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(currentFranchise));
        when(franchiseRepository.findByName(newName)).thenReturn(Mono.just(otherFranchise));

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId, newName))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.FRANCHISE_NAME_DUPLICATE
                )
                .verify();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(franchiseRepository, times(1)).findByName(newName);
        verify(franchiseRepository, never()).updateName(anyLong(), anyString());
    }

    @Test
    @DisplayName("Should handle whitespace in new name")
    void shouldHandleWhitespaceInNewName() {
        // Arrange
        Long franchiseId = 1L;
        String newNameWithSpaces = "  Updated Franchise  ";
        String trimmedName = "Updated Franchise";

        Franchise existingFranchise = Franchise.builder()
                .id(franchiseId)
                .name("Old Franchise")
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepository.findByName(trimmedName)).thenReturn(Mono.empty());
        when(franchiseRepository.updateName(franchiseId, trimmedName)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId, newNameWithSpaces))
                .expectNext(existingFranchise)
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(franchiseRepository, times(1)).findByName(trimmedName);
        verify(franchiseRepository, times(1)).updateName(franchiseId, trimmedName);
    }
}
