package co.com.bancolombia.usecase.updateproductname;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductNameUseCase - Unit Test")
class UpdateProductNameUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductNameUseCase updateProductNameUseCase;

    @Test
    @DisplayName("Should update product name successfully")
    void shouldUpdateProductNameSuccessfully() {
        // Given
        Long productId = 1L;
        Long branchId = 10L;
        String newName = "Updated Product";
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Original Product")
                .stock(50)
                .branchId(branchId)
                .build();
        Product updatedProduct = Product.builder()
                .id(productId)
                .name(newName)
                .stock(50)
                .branchId(branchId)
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.findByNameAndBranchId(anyString(), anyLong())).thenReturn(Mono.empty());
        when(productRepository.updateName(anyLong(), anyString())).thenReturn(Mono.empty());
        when(productRepository.findById(productId)).thenReturn(Mono.just(updatedProduct));

        // When & Then
        StepVerifier.create(updateProductNameUseCase.execute(productId, newName))
                .expectNextMatches(product ->
                        product.getId().equals(productId) &&
                        product.getName().equals(newName)
                )
                .verifyComplete();
    }
}
