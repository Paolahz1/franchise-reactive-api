package co.com.bancolombia.usecase.updateproductstock;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductStockUseCase - Complete Coverage Tests")
class UpdateProductStockUseCaseCompleteTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductStockUseCase useCase;

    @Test
    @DisplayName("Should update product stock successfully")
    void shouldUpdateProductStockSuccessfully() {
        // Arrange
        Long productId = 1L;
        Integer newStock = 200;

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .branchId(10L)
                .stock(100)
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.updateStock(productId, newStock)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(productId, newStock))
                .assertNext(updatedProduct -> {
                    assertThat(updatedProduct.getId()).isEqualTo(productId);
                    assertThat(updatedProduct.getName()).isEqualTo("Test Product");
                    assertThat(updatedProduct.getStock()).isEqualTo(newStock);
                    assertThat(updatedProduct.getBranchId()).isEqualTo(10L);
                })
                .verifyComplete();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).updateStock(productId, newStock);
    }

    @Test
    @DisplayName("Should update product stock to zero")
    void shouldUpdateProductStockToZero() {
        // Arrange
        Long productId = 1L;
        Integer newStock = 0;

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .branchId(10L)
                .stock(100)
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.updateStock(productId, newStock)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(productId, newStock))
                .assertNext(updatedProduct -> {
                    assertThat(updatedProduct.getId()).isEqualTo(productId);
                    assertThat(updatedProduct.getStock()).isEqualTo(0);
                })
                .verifyComplete();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).updateStock(productId, newStock);
    }

    @Test
    @DisplayName("Should throw BusinessException when product does not exist")
    void shouldThrowBusinessException_WhenProductDoesNotExist() {
        // Arrange
        Long productId = 999L;
        Integer newStock = 100;

        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(productId, newStock))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.PRODUCT_NOT_FOUND
                )
                .verify();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).updateStock(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Should propagate error when repository findById fails")
    void shouldPropagateError_WhenRepositoryFindByIdFails() {
        // Arrange
        Long productId = 1L;
        Integer newStock = 100;

        when(productRepository.findById(productId))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(productId, newStock))
                .expectError(RuntimeException.class)
                .verify();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).updateStock(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Should propagate error when repository updateStock fails")
    void shouldPropagateError_WhenRepositoryUpdateStockFails() {
        // Arrange
        Long productId = 1L;
        Integer newStock = 100;

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .branchId(10L)
                .stock(50)
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.updateStock(productId, newStock))
                .thenReturn(Mono.error(new RuntimeException("Update failed")));

        // Act & Assert
        StepVerifier.create(useCase.execute(productId, newStock))
                .expectError(RuntimeException.class)
                .verify();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).updateStock(productId, newStock);
    }
}
