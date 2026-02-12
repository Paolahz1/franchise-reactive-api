package co.com.bancolombia.usecase.addproducttobranch;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddProductToBranchUseCase - Complete Coverage Tests")
class AddProductToBranchUseCaseCompleteTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private AddProductToBranchUseCase useCase;

    @Test
    @DisplayName("Should add product successfully when branch exists and product name is unique")
    void shouldAddProductSuccessfully_WhenBranchExistsAndProductNameIsUnique() {
        // Arrange
        Long branchId = 1L;
        Product inputProduct = Product.builder()
                .name("New Product")
                .stock(100)
                .build();

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Test Branch")
                .franchiseId(10L)
                .build();

        Product savedProduct = Product.builder()
                .id(5L)
                .name("New Product")
                .stock(100)
                .branchId(branchId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findByNameAndBranchId(anyString(), anyLong())).thenReturn(Mono.empty());
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(savedProduct));

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .expectNext(savedProduct)
                .verifyComplete();

        verify(branchRepository, times(1)).findById(branchId);
        verify(productRepository, times(1)).findByNameAndBranchId("New Product", branchId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when branch does not exist")
    void shouldThrowBusinessException_WhenBranchDoesNotExist() {
        // Arrange
        Long branchId = 999L;
        Product inputProduct = Product.builder()
                .name("New Product")
                .stock(100)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.BRANCH_NOT_FOUND
                )
                .verify();

        verify(branchRepository, times(1)).findById(branchId);
        verify(productRepository, never()).findByNameAndBranchId(anyString(), anyLong());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when product name already exists in branch")
    void shouldThrowBusinessException_WhenProductNameAlreadyExistsInBranch() {
        // Arrange
        Long branchId = 1L;
        Product inputProduct = Product.builder()
                .name("Existing Product")
                .stock(100)
                .build();

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Test Branch")
                .franchiseId(10L)
                .build();

        Product existingProduct = Product.builder()
                .id(3L)
                .name("Existing Product")
                .stock(50)
                .branchId(branchId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findByNameAndBranchId(anyString(), anyLong()))
                .thenReturn(Mono.just(existingProduct));

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.PRODUCT_NAME_DUPLICATE
                )
                .verify();

        verify(branchRepository, times(1)).findById(branchId);
        verify(productRepository, times(1)).findByNameAndBranchId("Existing Product", branchId);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate error when branch repository fails")
    void shouldPropagateError_WhenBranchRepositoryFails() {
        // Arrange
        Long branchId = 1L;
        Product inputProduct = Product.builder()
                .name("New Product")
                .stock(100)
                .build();

        when(branchRepository.findById(branchId))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .expectError(RuntimeException.class)
                .verify();

        verify(branchRepository, times(1)).findById(branchId);
        verify(productRepository, never()).findByNameAndBranchId(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should propagate error when product save fails")
    void shouldPropagateError_WhenProductSaveFails() {
        // Arrange
        Long branchId = 1L;
        Product inputProduct = Product.builder()
                .name("New Product")
                .stock(100)
                .build();

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Test Branch")
                .franchiseId(10L)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findByNameAndBranchId(anyString(), anyLong())).thenReturn(Mono.empty());
        when(productRepository.save(any(Product.class)))
                .thenReturn(Mono.error(new RuntimeException("Save failed")));

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .expectError(RuntimeException.class)
                .verify();

        verify(branchRepository, times(1)).findById(branchId);
        verify(productRepository, times(1)).findByNameAndBranchId("New Product", branchId);
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
