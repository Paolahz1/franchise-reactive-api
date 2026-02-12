package co.com.bancolombia.mysql.adapter;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.mysql.entity.ProductEntity;
import co.com.bancolombia.mysql.mapper.ProductMapper;
import co.com.bancolombia.mysql.repository.ProductR2dbcRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductMySQLAdapter - Unit Tests")
class ProductMySQLAdapterTest {

    @Mock
    private ProductR2dbcRepository r2dbcRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductMySQLAdapter adapter;

    @Test
    @DisplayName("Should save product successfully")
    void shouldSaveProductSuccessfully() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .stock(100)
                .branchId(1L)
                .build();

        ProductEntity entity = new ProductEntity();
        entity.setName("Test Product");
        entity.setStock(100);
        entity.setBranchId(1L);

        ProductEntity savedEntity = new ProductEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Test Product");
        savedEntity.setStock(100);
        savedEntity.setBranchId(1L);

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(100)
                .branchId(1L)
                .build();

        when(productMapper.toEntity(product)).thenReturn(entity);
        when(r2dbcRepository.save(entity)).thenReturn(Mono.just(savedEntity));
        when(productMapper.toDomain(savedEntity)).thenReturn(savedProduct);

        // Act & Assert
        StepVerifier.create(adapter.save(product))
                .expectNext(savedProduct)
                .verifyComplete();

        verify(productMapper).toEntity(product);
        verify(r2dbcRepository).save(entity);
        verify(productMapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("Should find product by ID successfully")
    void shouldFindProductByIdSuccessfully() {
        // Arrange
        Long productId = 1L;

        ProductEntity entity = new ProductEntity();
        entity.setId(productId);
        entity.setName("Test Product");
        entity.setStock(100);
        entity.setBranchId(1L);

        Product product = Product.builder()
                .id(productId)
                .name("Test Product")
                .stock(100)
                .branchId(1L)
                .build();

        when(r2dbcRepository.findById(productId)).thenReturn(Mono.just(entity));
        when(productMapper.toDomain(entity)).thenReturn(product);

        // Act & Assert
        StepVerifier.create(adapter.findById(productId))
                .expectNext(product)
                .verifyComplete();

        verify(r2dbcRepository).findById(productId);
        verify(productMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should find product by name and branch ID successfully")
    void shouldFindProductByNameAndBranchIdSuccessfully() {
        // Arrange
        String name = "Test Product";
        Long branchId = 1L;

        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName(name);
        entity.setStock(100);
        entity.setBranchId(branchId);

        Product product = Product.builder()
                .id(1L)
                .name(name)
                .stock(100)
                .branchId(branchId)
                .build();

        when(r2dbcRepository.findByNameAndBranchId(name, branchId)).thenReturn(Mono.just(entity));
        when(productMapper.toDomain(entity)).thenReturn(product);

        // Act & Assert
        StepVerifier.create(adapter.findByNameAndBranchId(name, branchId))
                .expectNext(product)
                .verifyComplete();

        verify(r2dbcRepository).findByNameAndBranchId(name, branchId);
        verify(productMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should delete product by ID successfully")
    void shouldDeleteProductByIdSuccessfully() {
        // Arrange
        Long productId = 1L;

        when(r2dbcRepository.deleteById(productId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.deleteById(productId))
                .verifyComplete();

        verify(r2dbcRepository).deleteById(productId);
    }

    @Test
    @DisplayName("Should update product stock successfully")
    void shouldUpdateProductStockSuccessfully() {
        // Arrange
        Long productId = 1L;
        Integer newStock = 200;

        when(r2dbcRepository.updateStock(productId, newStock)).thenReturn(Mono.just(1));

        // Act & Assert
        StepVerifier.create(adapter.updateStock(productId, newStock))
                .verifyComplete();

        verify(r2dbcRepository).updateStock(productId, newStock);
    }

    @Test
    @DisplayName("Should update product name successfully")
    void shouldUpdateProductNameSuccessfully() {
        // Arrange
        Long productId = 1L;
        String newName = "Updated Product";

        when(r2dbcRepository.updateName(productId, newName)).thenReturn(Mono.just(1));

        // Act & Assert
        StepVerifier.create(adapter.updateName(productId, newName))
                .verifyComplete();

        verify(r2dbcRepository).updateName(productId, newName);
    }

    @Test
    @DisplayName("Should find max stock products by franchise successfully")
    void shouldFindMaxStockProductsByFranchiseSuccessfully() {
        // Arrange
        Long franchiseId = 1L;

        ProductEntity entity1 = new ProductEntity();
        entity1.setId(1L);
        entity1.setName("Product 1");
        entity1.setStock(500);
        entity1.setBranchId(10L);

        ProductEntity entity2 = new ProductEntity();
        entity2.setId(2L);
        entity2.setName("Product 2");
        entity2.setStock(800);
        entity2.setBranchId(20L);

        Product product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .stock(500)
                .branchId(10L)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .stock(800)
                .branchId(20L)
                .build();

        when(r2dbcRepository.findMaxStockByFranchise(franchiseId))
                .thenReturn(Flux.just(entity1, entity2));
        when(productMapper.toDomain(entity1)).thenReturn(product1);
        when(productMapper.toDomain(entity2)).thenReturn(product2);

        // Act & Assert
        StepVerifier.create(adapter.findMaxStockByFranchise(franchiseId))
                .expectNext(product1)
                .expectNext(product2)
                .verifyComplete();

        verify(r2dbcRepository).findMaxStockByFranchise(franchiseId);
        verify(productMapper, times(2)).toDomain(any());
    }
}
