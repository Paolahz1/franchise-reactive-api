package co.com.bancolombia.usecase.updateproductstock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductStockUseCase - Unit Test")
class UpdateProductStockUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductStockUseCase updateProductStockUseCase;

    @Test
    @DisplayName("Should update product stock successfully")
    void shouldUpdateProductStockSuccessfully() {
        // Given
        Long productId = 1L;
        Integer newStock = 200;
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Product A")
                .stock(100)
                .branchId(10L)
                .build();
        Product updatedProduct = Product.builder()
                .id(productId)
                .name("Product A")
                .stock(newStock)
                .branchId(10L)
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.updateStock(anyLong(), anyInt())).thenReturn(Mono.empty());
        when(productRepository.findById(productId)).thenReturn(Mono.just(updatedProduct));

        // When & Then
        StepVerifier.create(updateProductStockUseCase.execute(productId, newStock))
                .expectNextMatches(product ->
                        product.getId().equals(productId) &&
                        product.getStock().equals(newStock)
                )
                .verifyComplete();
    }
}
