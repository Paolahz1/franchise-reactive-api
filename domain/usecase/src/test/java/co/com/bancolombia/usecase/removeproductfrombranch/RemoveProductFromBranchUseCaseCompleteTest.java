package co.com.bancolombia.usecase.removeproductfrombranch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RemoveProductFromBranchUseCase - Complete Coverage Tests")
class RemoveProductFromBranchUseCaseCompleteTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private RemoveProductFromBranchUseCase useCase;

    @Test
    @DisplayName("Should remove product from branch successfully")
    void shouldRemoveProductFromBranchSuccessfully() {
        // Arrange
        Long branchId = 1L;
        Long productId = 10L;

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Test Branch")
                .franchiseId(100L)
                .build();

        Product product = Product.builder()
                .id(productId)
                .name("Test Product")
                .branchId(branchId)
                .stock(50)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productRepository.deleteById(productId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, productId))
                .verifyComplete();

        verify(branchRepository, times(1)).findById(branchId);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("Should throw BusinessException when branch does not exist")
    void shouldThrowBusinessException_WhenBranchDoesNotExist() {
        // Arrange
        Long branchId = 999L;
        Long productId = 10L;

        when(branchRepository.findById(branchId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, productId))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.BRANCH_NOT_FOUND
                )
                .verify();

        verify(branchRepository, times(1)).findById(branchId);
        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw BusinessException when product does not exist")
    void shouldThrowBusinessException_WhenProductDoesNotExist() {
        // Arrange
        Long branchId = 1L;
        Long productId = 999L;

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Test Branch")
                .franchiseId(100L)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, productId))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.PRODUCT_NOT_FOUND
                )
                .verify();

        verify(branchRepository, times(1)).findById(branchId);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw BusinessException when product does not belong to branch")
    void shouldThrowBusinessException_WhenProductDoesNotBelongToBranch() {
        // Arrange
        Long branchId = 1L;
        Long productId = 10L;
        Long differentBranchId = 2L;

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Test Branch")
                .franchiseId(100L)
                .build();

        Product product = Product.builder()
                .id(productId)
                .name("Test Product")
                .branchId(differentBranchId)  // Different branch
                .stock(50)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findById(productId)).thenReturn(Mono.just(product));

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, productId))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.PRODUCT_REMOVAL_ERROR
                )
                .verify();

        verify(branchRepository, times(1)).findById(branchId);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should propagate error when branch repository fails")
    void shouldPropagateError_WhenBranchRepositoryFails() {
        // Arrange
        Long branchId = 1L;
        Long productId = 10L;

        when(branchRepository.findById(branchId))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, productId))
                .expectError(RuntimeException.class)
                .verify();

        verify(branchRepository, times(1)).findById(branchId);
        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should propagate error when product repository findById fails")
    void shouldPropagateError_WhenProductRepositoryFindByIdFails() {
        // Arrange
        Long branchId = 1L;
        Long productId = 10L;

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Test Branch")
                .franchiseId(100L)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findById(productId))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, productId))
                .expectError(RuntimeException.class)
                .verify();

        verify(branchRepository, times(1)).findById(branchId);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should propagate error when product repository deleteById fails")
    void shouldPropagateError_WhenProductRepositoryDeleteByIdFails() {
        // Arrange
        Long branchId = 1L;
        Long productId = 10L;

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Test Branch")
                .franchiseId(100L)
                .build();

        Product product = Product.builder()
                .id(productId)
                .name("Test Product")
                .branchId(branchId)
                .stock(50)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productRepository.deleteById(productId))
                .thenReturn(Mono.error(new RuntimeException("Delete failed")));

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, productId))
                .expectError(RuntimeException.class)
                .verify();

        verify(branchRepository, times(1)).findById(branchId);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }
}
