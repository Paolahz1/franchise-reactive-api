package co.com.bancolombia.usecase.createfranchise;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateFranchiseUseCase - Complete Coverage Tests")
class CreateFranchiseUseCaseCompleteTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private CreateFranchiseUseCase useCase;

    @Test
    @DisplayName("Should create franchise successfully when name does not exist")
    void shouldCreateFranchiseSuccessfully_WhenNameDoesNotExist() {
        // Arrange
        Franchise inputFranchise = Franchise.builder()
                .name("New Franchise")
                .build();

        Franchise savedFranchise = Franchise.builder()
                .id(1L)
                .name("New Franchise")
                .build();

        when(franchiseRepository.findByName(anyString())).thenReturn(Mono.empty());
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchise));

        // Act & Assert
        StepVerifier.create(useCase.execute(inputFranchise))
                .expectNext(savedFranchise)
                .verifyComplete();

        verify(franchiseRepository, times(1)).findByName("New Franchise");
        verify(franchiseRepository, times(1)).save(inputFranchise);
        verifyNoMoreInteractions(franchiseRepository);
    }

    @Test
    @DisplayName("Should throw BusinessException when franchise name already exists")
    void shouldThrowBusinessException_WhenFranchiseNameAlreadyExists() {
        // Arrange
        Franchise inputFranchise = Franchise.builder()
                .name("Existing Franchise")
                .build();

        Franchise existingFranchise = Franchise.builder()
                .id(1L)
                .name("Existing Franchise")
                .build();

        when(franchiseRepository.findByName(anyString())).thenReturn(Mono.just(existingFranchise));

        // Act & Assert
        StepVerifier.create(useCase.execute(inputFranchise))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.FRANCHISE_NAME_ALREADY_EXISTS
                )
                .verify();

        verify(franchiseRepository, times(1)).findByName("Existing Franchise");
        verify(franchiseRepository, never()).save(any());
        verifyNoMoreInteractions(franchiseRepository);
    }

    @Test
    @DisplayName("Should propagate error when repository findByName fails")
    void shouldPropagateError_WhenRepositoryFindByNameFails() {
        // Arrange
        Franchise inputFranchise = Franchise.builder()
                .name("Test Franchise")
                .build();

        when(franchiseRepository.findByName(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(inputFranchise))
                .expectError(RuntimeException.class)
                .verify();

        verify(franchiseRepository, times(1)).findByName("Test Franchise");
        verify(franchiseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate error when repository save fails")
    void shouldPropagateError_WhenRepositorySaveFails() {
        // Arrange
        Franchise inputFranchise = Franchise.builder()
                .name("Test Franchise")
                .build();

        when(franchiseRepository.findByName(anyString())).thenReturn(Mono.empty());
        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Save failed")));

        // Act & Assert
        StepVerifier.create(useCase.execute(inputFranchise))
                .expectError(RuntimeException.class)
                .verify();

        verify(franchiseRepository, times(1)).findByName("Test Franchise");
        verify(franchiseRepository, times(1)).save(inputFranchise);
    }
}
