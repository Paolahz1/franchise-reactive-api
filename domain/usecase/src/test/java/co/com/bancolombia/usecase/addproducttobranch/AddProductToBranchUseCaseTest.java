package co.com.bancolombia.usecase.addproducttobranch;

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
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddProductToBranchUseCase - Unit Test")
class AddProductToBranchUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private AddProductToBranchUseCase addProductToBranchUseCase;

    @Test
    @DisplayName("Should add product to branch successfully")
    void shouldAddProductToBranchSuccessfully() {
        // Arrange
        Long branchId = 1L;
        String productName = "Product A";
        Integer stock = 100;
        Branch branch = Branch.builder()
                .id(branchId)
                .name("Downtown Branch")
                .franchiseId(10L)
                .build();
        Product newProduct = Product.builder()
                .id(1L)
                .name(productName)
                .stock(stock)
                .branchId(branchId)
                .build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findByNameAndBranchId(anyString(), anyLong())).thenReturn(Mono.empty());
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(newProduct));

        // When & Then
        StepVerifier.create(addProductToBranchUseCase.execute(branchId,newProduct))
                .expectNextMatches(product ->
                        product.getName().equals(productName) &&
                        product.getStock().equals(stock) &&
                        product.getBranchId().equals(branchId)
                )
                .verifyComplete();
    }
}
