package co.com.bancolombia.usecase.addbranchtofranchise;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddBranchToFranchiseUseCase - Unit Test")
class AddBranchToFranchiseUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;

    @Test
    @DisplayName("Should add branch to franchise successfully")
    void shouldAddBranchToFranchiseSuccessfully() {
        // Given
        Long franchiseId = 1L;
        String branchName = "Downtown Branch";
        Franchise franchise = Franchise.builder()
                .id(franchiseId)
                .name("My Franchise")
                .build();
        Branch newBranch = Branch.builder()
                .id(1L)
                .name(branchName)
                .franchiseId(franchiseId)
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchRepository.findByNameAndFranchiseId(anyString(), anyLong())).thenReturn(Mono.empty());
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(newBranch));

        // Then
        StepVerifier.create(addBranchToFranchiseUseCase.execute(franchiseId, newBranch))
                .expectNextMatches(branch ->
                        branch.getName().equals(branchName) &&
                        branch.getFranchiseId().equals(franchiseId)
                )
                .verifyComplete();
    }
}
