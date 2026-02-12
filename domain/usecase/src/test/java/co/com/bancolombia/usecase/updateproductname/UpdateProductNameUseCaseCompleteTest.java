package co.com.bancolombia.usecase.updateproductname;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductNameUseCase - Complete Coverage Tests")
class UpdateProductNameUseCaseCompleteTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductNameUseCase useCase;

    @Test
    @DisplayName("Should update product name successfully when new name is unique")
    void shouldUpdateProductNameSuccessfully_WhenNewNameIsUnique() {
        // Arrange
        Long productId = 1L;
        Long branchId = 10L;
        String newName = "Updated Product";

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Old Product")
                .branchId(branchId)
                .stock(100)
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.findByNameAndBranchId(newName, branchId)).thenReturn(Mono.empty());
        when(productRepository.updateName(productId, newName)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(productId, newName))
                .expectNext(existingProduct)
                .verifyComplete();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).findByNameAndBranchId(newName, branchId);
        verify(productRepository, times(1)).updateName(productId, newName);
    }

    @Test
    @DisplayName("Should update product name successfully when updating to same name (same ID)")
    void shouldUpdateProductNameSuccessfully_WhenUpdatingToSameName() {
        // Arrange
        Long productId = 1L;
        Long branchId = 10L;
        String sameName = "Same Product";

        Product existingProduct = Product.builder()
                .id(productId)
                .name(sameName)
                .branchId(branchId)
                .stock(100)
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.findByNameAndBranchId(sameName, branchId))
                .thenReturn(Mono.just(existingProduct));
        when(productRepository.updateName(productId, sameName)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(productId, sameName))
                .expectNext(existingProduct)
                .verifyComplete();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).findByNameAndBranchId(sameName, branchId);
        verify(productRepository, times(1)).updateName(productId, sameName);
    }

    @Test
    @DisplayName("Should throw BusinessException when product does not exist")
    void shouldThrowBusinessException_WhenProductDoesNotExist() {
        // Arrange
        Long productId = 999L;
        String newName = "New Name";

        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(productId, newName))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.PRODUCT_NOT_FOUND
                )
                .verify();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).findByNameAndBranchId(anyString(), anyLong());
        verify(productRepository, never()).updateName(anyLong(), anyString());
    }

    @Test
    @DisplayName("Should throw BusinessException when new name already exists in branch (different ID)")
    void shouldThrowBusinessException_WhenNewNameAlreadyExistsInBranch() {
        // Arrange
        Long productId = 1L;
        Long branchId = 10L;
        String newName = "Existing Product";

        Product currentProduct = Product.builder()
                .id(productId)
                .name("Old Product")
                .branchId(branchId)
                .stock(100)
                .build();

        Product otherProduct = Product.builder()
                .id(2L)  // Different ID
                .name(newName)
                .branchId(branchId)
                .stock(50)
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(currentProduct));
        when(productRepository.findByNameAndBranchId(newName, branchId))
                .thenReturn(Mono.just(otherProduct));

        // Act & Assert
        StepVerifier.create(useCase.execute(productId, newName))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.PRODUCT_NAME_DUPLICATE
                )
                .verify();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).findByNameAndBranchId(newName, branchId);
        verify(productRepository, never()).updateName(anyLong(), anyString());
    }

    @Test
    @DisplayName("Should handle whitespace in new name")
    void shouldHandleWhitespaceInNewName() {
        // Arrange
        Long productId = 1L;
        Long branchId = 10L;
        String newNameWithSpaces = "  Updated Product  ";
        String trimmedName = "Updated Product";

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Old Product")
                .branchId(branchId)
                .stock(100)
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.findByNameAndBranchId(trimmedName, branchId)).thenReturn(Mono.empty());
        when(productRepository.updateName(productId, trimmedName)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(productId, newNameWithSpaces))
                .expectNext(existingProduct)
                .verifyComplete();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).findByNameAndBranchId(trimmedName, branchId);
        verify(productRepository, times(1)).updateName(productId, trimmedName);
    }
}
