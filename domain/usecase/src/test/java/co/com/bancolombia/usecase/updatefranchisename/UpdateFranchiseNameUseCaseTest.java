package co.com.bancolombia.usecase.updatefranchisename;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateFranchiseNameUseCase - Unit Test")
class UpdateFranchiseNameUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    @Test
    @DisplayName("Should update franchise name successfully")
    void shouldUpdateFranchiseNameSuccessfully() {
        // Given
        Long franchiseId = 1L;
        String newName = "New Franchise";
        Franchise existingFranchise = Franchise.builder()
                .id(franchiseId)
                .name("Original Franchise")
                .build();
        Franchise updatedFranchise = Franchise.builder()
                .id(franchiseId)
                .name(newName)
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(franchiseRepository.findByName(anyString())).thenReturn(Mono.empty());
        when(franchiseRepository.updateName(anyLong(), anyString())).thenReturn(Mono.empty());
        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(updatedFranchise));

        // When & Then
        StepVerifier.create(updateFranchiseNameUseCase.execute(franchiseId, newName))
                .expectNextMatches(franchise ->
                        franchise.getId().equals(franchiseId) &&
                        franchise.getName().equals(newName)
                )
                .verifyComplete();
    }
}
