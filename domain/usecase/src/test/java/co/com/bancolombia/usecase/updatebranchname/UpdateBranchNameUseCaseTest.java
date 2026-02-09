package co.com.bancolombia.usecase.updatebranchname;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateBranchNameUseCase - Unit Test")
class UpdateBranchNameUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private UpdateBranchNameUseCase updateBranchNameUseCase;

    @Test
    @DisplayName("Should update branch name successfully")
    void shouldUpdateBranchNameSuccessfully() {
        // Given
        Long branchId = 1L;
        Long franchiseId = 10L;
        String newName = "New Branch";
        Branch existingBranch = Branch.builder()
                .id(branchId)
                .name("Original Branch")
                .franchiseId(franchiseId)
                .build();
        Branch updatedBranch = Branch.builder()
                .id(branchId)
                .name(newName)
                .franchiseId(franchiseId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(branchRepository.findByNameAndFranchiseId(anyString(), anyLong())).thenReturn(Mono.empty());
        when(branchRepository.updateName(anyLong(), anyString())).thenReturn(Mono.empty());
        when(branchRepository.findById(branchId)).thenReturn(Mono.just(updatedBranch));

        // When & Then
        StepVerifier.create(updateBranchNameUseCase.execute(branchId, newName))
                .expectNextMatches(branch ->
                        branch.getId().equals(branchId) &&
                        branch.getName().equals(newName)
                )
                .verifyComplete();
    }
}
