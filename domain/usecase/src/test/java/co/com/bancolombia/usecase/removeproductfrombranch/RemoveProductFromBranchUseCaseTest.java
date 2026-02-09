package co.com.bancolombia.usecase.removeproductfrombranch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("RemoveProductFromBranchUseCase - Unit Test")
class RemoveProductFromBranchUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private RemoveProductFromBranchUseCase removeProductFromBranchUseCase;

    @Test
    @DisplayName("Should remove product from branch successfully")
    void shouldRemoveProductFromBranchSuccessfully() {
        // Given
        Long branchId = 1L;
        Long productId = 10L;
        Branch branch = Branch.builder()
                .id(branchId)
                .name("Downtown Branch")
                .franchiseId(100L)
                .build();
        Product product = Product.builder()
                .id(productId)
                .name("Product A")
                .stock(50)
                .branchId(branchId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productRepository.deleteById(anyLong())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(removeProductFromBranchUseCase.execute(branchId, productId))
                .verifyComplete();
    }
}
