package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.request.ProductRequest;
import co.com.bancolombia.api.dto.request.UpdateNameRequest;
import co.com.bancolombia.api.dto.request.UpdateStockRequest;
import co.com.bancolombia.api.dto.response.ProductResponse;
import co.com.bancolombia.api.mapper.ProductRequestMapper;
import co.com.bancolombia.api.mapper.ProductResponseMapper;
import co.com.bancolombia.api.utils.LoggingUtils;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.usecase.addproducttobranch.AddProductToBranchUseCase;
import co.com.bancolombia.usecase.removeproductfrombranch.RemoveProductFromBranchUseCase;
import co.com.bancolombia.usecase.updateproductname.UpdateProductNameUseCase;
import co.com.bancolombia.usecase.updateproductstock.UpdateProductStockUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductHandler - Unit Tests")
class ProductHandlerTest {

    @Mock
    private AddProductToBranchUseCase addProductToBranchUseCase;

    @Mock
    private RemoveProductFromBranchUseCase removeProductFromBranchUseCase;

    @Mock
    private UpdateProductStockUseCase updateProductStockUseCase;

    @Mock
    private UpdateProductNameUseCase updateProductNameUseCase;

    @Mock
    private ProductRequestMapper productRequestMapper;

    @Mock
    private ProductResponseMapper productResponseMapper;

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private ProductHandler handler;

    @Test
    @DisplayName("Should add product to branch successfully")
    void shouldAddProductToBranchSuccessfully() {
        // Arrange
        Long branchId = 1L;
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setStock(100);

        Product product = Product.builder()
                .name("Test Product")
                .stock(100)
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(100)
                .branchId(branchId)
                .build();

        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Test Product");
        response.setStock(100);

        when(serverRequest.pathVariable("branchId")).thenReturn(String.valueOf(branchId));
        when(serverRequest.bodyToMono(ProductRequest.class)).thenReturn(Mono.just(request));
        when(productRequestMapper.toDomain(request)).thenReturn(product);
        when(addProductToBranchUseCase.execute(branchId, product)).thenReturn(Mono.just(savedProduct));
        when(productResponseMapper.toResponse(savedProduct)).thenReturn(response);

        // Act & Assert
        StepVerifier.create(handler.addProductToBranch(serverRequest))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(loggingUtils).logRequest("ADD_PRODUCT_TO_BRANCH", serverRequest);
        verify(addProductToBranchUseCase).execute(branchId, product);
    }

    @Test
    @DisplayName("Should remove product from branch successfully")
    void shouldRemoveProductFromBranchSuccessfully() {
        // Arrange
        Long branchId = 1L;
        Long productId = 2L;

        when(serverRequest.pathVariable("branchId")).thenReturn(String.valueOf(branchId));
        when(serverRequest.pathVariable("productId")).thenReturn(String.valueOf(productId));
        when(removeProductFromBranchUseCase.execute(branchId, productId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(handler.removeProductFromBranch(serverRequest))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.NO_CONTENT
                )
                .verifyComplete();

        verify(loggingUtils).logRequest("REMOVE_PRODUCT_FROM_BRANCH", serverRequest);
        verify(removeProductFromBranchUseCase).execute(branchId, productId);
    }

    @Test
    @DisplayName("Should update product stock successfully")
    void shouldUpdateProductStockSuccessfully() {
        // Arrange
        Long productId = 1L;
        UpdateStockRequest request = new UpdateStockRequest();
        request.setStock(200);

        Product updatedProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .stock(200)
                .branchId(1L)
                .build();

        ProductResponse response = new ProductResponse();
        response.setId(productId);
        response.setName("Test Product");
        response.setStock(200);

        when(serverRequest.pathVariable("productId")).thenReturn(String.valueOf(productId));
        when(serverRequest.bodyToMono(UpdateStockRequest.class)).thenReturn(Mono.just(request));
        when(updateProductStockUseCase.execute(productId, 200)).thenReturn(Mono.just(updatedProduct));
        when(productResponseMapper.toResponse(updatedProduct)).thenReturn(response);

        // Act & Assert
        StepVerifier.create(handler.updateProductStock(serverRequest))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.OK
                )
                .verifyComplete();

        verify(loggingUtils).logRequest("UPDATE_PRODUCT_STOCK", serverRequest);
        verify(updateProductStockUseCase).execute(productId, 200);
    }

    @Test
    @DisplayName("Should update product name successfully")
    void shouldUpdateProductNameSuccessfully() {
        // Arrange
        Long productId = 1L;
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Updated Product");

        Product updatedProduct = Product.builder()
                .id(productId)
                .name("Updated Product")
                .stock(100)
                .branchId(1L)
                .build();

        ProductResponse response = new ProductResponse();
        response.setId(productId);
        response.setName("Updated Product");
        response.setStock(100);

        when(serverRequest.pathVariable("productId")).thenReturn(String.valueOf(productId));
        when(serverRequest.bodyToMono(UpdateNameRequest.class)).thenReturn(Mono.just(request));
        when(updateProductNameUseCase.execute(productId, "Updated Product")).thenReturn(Mono.just(updatedProduct));
        when(productResponseMapper.toResponse(updatedProduct)).thenReturn(response);

        // Act & Assert
        StepVerifier.create(handler.updateProductName(serverRequest))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.OK
                )
                .verifyComplete();

        verify(loggingUtils).logRequest("UPDATE_PRODUCT_NAME", serverRequest);
        verify(updateProductNameUseCase).execute(productId, "Updated Product");
    }

    @Test
    @DisplayName("Should handle error when adding product with empty body")
    void shouldHandleErrorWhenAddingProductWithEmptyBody() {
        // Arrange
        Long branchId = 1L;
        when(serverRequest.pathVariable("branchId")).thenReturn(String.valueOf(branchId));
        when(serverRequest.bodyToMono(ProductRequest.class)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(handler.addProductToBranch(serverRequest))
                .expectError()
                .verify();

        verify(loggingUtils).logRequest("ADD_PRODUCT_TO_BRANCH", serverRequest);
    }

    @Test
    @DisplayName("Should handle error when removing product fails")
    void shouldHandleErrorWhenRemovingProductFails() {
        // Arrange
        Long branchId = 1L;
        Long productId = 2L;
        RuntimeException error = new RuntimeException("Database error");

        when(serverRequest.pathVariable("branchId")).thenReturn(String.valueOf(branchId));
        when(serverRequest.pathVariable("productId")).thenReturn(String.valueOf(productId));
        when(removeProductFromBranchUseCase.execute(branchId, productId)).thenReturn(Mono.error(error));

        // Act & Assert
        StepVerifier.create(handler.removeProductFromBranch(serverRequest))
                .expectError(RuntimeException.class)
                .verify();

        verify(loggingUtils).logRequest("REMOVE_PRODUCT_FROM_BRANCH", serverRequest);
        verify(loggingUtils).logError("REMOVE_PRODUCT_FROM_BRANCH", error);
    }

    @Test
    @DisplayName("Should handle error when updating product stock with empty body")
    void shouldHandleErrorWhenUpdatingStockWithEmptyBody() {
        // Arrange
        Long productId = 1L;
        when(serverRequest.pathVariable("productId")).thenReturn(String.valueOf(productId));
        when(serverRequest.bodyToMono(UpdateStockRequest.class)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(handler.updateProductStock(serverRequest))
                .expectError()
                .verify();

        verify(loggingUtils).logRequest("UPDATE_PRODUCT_STOCK", serverRequest);
    }

    @Test
    @DisplayName("Should handle error when updating product stock fails")
    void shouldHandleErrorWhenUpdatingStockFails() {
        // Arrange
        Long productId = 1L;
        UpdateStockRequest request = new UpdateStockRequest();
        request.setStock(200);
        RuntimeException error = new RuntimeException("Database error");

        when(serverRequest.pathVariable("productId")).thenReturn(String.valueOf(productId));
        when(serverRequest.bodyToMono(UpdateStockRequest.class)).thenReturn(Mono.just(request));
        when(updateProductStockUseCase.execute(productId, 200)).thenReturn(Mono.error(error));

        // Act & Assert
        StepVerifier.create(handler.updateProductStock(serverRequest))
                .expectError(RuntimeException.class)
                .verify();

        verify(loggingUtils).logRequest("UPDATE_PRODUCT_STOCK", serverRequest);
        verify(loggingUtils).logError("UPDATE_PRODUCT_STOCK", error);
    }

    @Test
    @DisplayName("Should handle error when updating product name with empty body")
    void shouldHandleErrorWhenUpdatingNameWithEmptyBody() {
        // Arrange
        Long productId = 1L;
        when(serverRequest.pathVariable("productId")).thenReturn(String.valueOf(productId));
        when(serverRequest.bodyToMono(UpdateNameRequest.class)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(handler.updateProductName(serverRequest))
                .expectError()
                .verify();

        verify(loggingUtils).logRequest("UPDATE_PRODUCT_NAME", serverRequest);
    }

    @Test
    @DisplayName("Should handle error when updating product name fails")
    void shouldHandleErrorWhenUpdatingNameFails() {
        // Arrange
        Long productId = 1L;
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Updated Product");
        RuntimeException error = new RuntimeException("Database error");

        when(serverRequest.pathVariable("productId")).thenReturn(String.valueOf(productId));
        when(serverRequest.bodyToMono(UpdateNameRequest.class)).thenReturn(Mono.just(request));
        when(updateProductNameUseCase.execute(productId, "Updated Product")).thenReturn(Mono.error(error));

        // Act & Assert
        StepVerifier.create(handler.updateProductName(serverRequest))
                .expectError(RuntimeException.class)
                .verify();

        verify(loggingUtils).logRequest("UPDATE_PRODUCT_NAME", serverRequest);
        verify(loggingUtils).logError("UPDATE_PRODUCT_NAME", error);
    }
}
