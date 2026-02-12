package co.com.bancolombia.usecase.getmaxstockproductsbyfranchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.BranchWithTopProduct;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.FranchiseWithTopProducts;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetMaxStockProductsByFranchiseUseCase - Complete Coverage Tests")
class GetMaxStockProductsByFranchiseUseCaseCompleteTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private GetMaxStockProductsByFranchiseUseCase useCase;

    @Test
    @DisplayName("Should get max stock products by franchise successfully")
    void shouldGetMaxStockProductsByFranchiseSuccessfully() {
        // Arrange
        Long franchiseId = 1L;

        Franchise franchise = Franchise.builder()
                .id(franchiseId)
                .name("Test Franchise")
                .build();

        Branch branch1 = Branch.builder()
                .id(10L)
                .name("Branch 1")
                .franchiseId(franchiseId)
                .build();

        Branch branch2 = Branch.builder()
                .id(20L)
                .name("Branch 2")
                .franchiseId(franchiseId)
                .build();

        Product product1 = Product.builder()
                .id(100L)
                .name("Product 1")
                .branchId(10L)
                .stock(500)
                .build();

        Product product2 = Product.builder()
                .id(200L)
                .name("Product 2")
                .branchId(20L)
                .stock(300)
                .build();

        BranchWithTopProduct branchWithProduct1 = BranchWithTopProduct.builder()
                .branch(branch1)
                .topProduct(product1)
                .build();

        BranchWithTopProduct branchWithProduct2 = BranchWithTopProduct.builder()
                .branch(branch2)
                .topProduct(product2)
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchRepository.findBranchesWithTopProductByFranchiseId(franchiseId))
                .thenReturn(Flux.just(branchWithProduct1, branchWithProduct2));

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getFranchise()).isEqualTo(franchise);
                    assertThat(result.getBranchesWithTopProducts()).hasSize(2);
                    assertThat(result.getBranchesWithTopProducts().get(0)).isEqualTo(branchWithProduct1);
                    assertThat(result.getBranchesWithTopProducts().get(1)).isEqualTo(branchWithProduct2);
                })
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(branchRepository, times(1)).findBranchesWithTopProductByFranchiseId(franchiseId);
    }

    @Test
    @DisplayName("Should return empty list when franchise has no branches with products")
    void shouldReturnEmptyList_WhenFranchiseHasNoBranchesWithProducts() {
        // Arrange
        Long franchiseId = 1L;

        Franchise franchise = Franchise.builder()
                .id(franchiseId)
                .name("Test Franchise")
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchRepository.findBranchesWithTopProductByFranchiseId(franchiseId))
                .thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getFranchise()).isEqualTo(franchise);
                    assertThat(result.getBranchesWithTopProducts()).isEmpty();
                })
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(branchRepository, times(1)).findBranchesWithTopProductByFranchiseId(franchiseId);
    }

    @Test
    @DisplayName("Should throw BusinessException when franchise does not exist")
    void shouldThrowBusinessException_WhenFranchiseDoesNotExist() {
        // Arrange
        Long franchiseId = 999L;

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.FRANCHISE_NOT_FOUND
                )
                .verify();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(branchRepository, never()).findBranchesWithTopProductByFranchiseId(anyLong());
    }

    @Test
    @DisplayName("Should propagate error when franchise repository fails")
    void shouldPropagateError_WhenFranchiseRepositoryFails() {
        // Arrange
        Long franchiseId = 1L;

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId))
                .expectError(RuntimeException.class)
                .verify();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(branchRepository, never()).findBranchesWithTopProductByFranchiseId(anyLong());
    }

    @Test
    @DisplayName("Should propagate error when branch repository fails")
    void shouldPropagateError_WhenBranchRepositoryFails() {
        // Arrange
        Long franchiseId = 1L;

        Franchise franchise = Franchise.builder()
                .id(franchiseId)
                .name("Test Franchise")
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchRepository.findBranchesWithTopProductByFranchiseId(franchiseId))
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId))
                .expectError(RuntimeException.class)
                .verify();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(branchRepository, times(1)).findBranchesWithTopProductByFranchiseId(franchiseId);
    }

    @Test
    @DisplayName("Should handle single branch with top product")
    void shouldHandleSingleBranchWithTopProduct() {
        // Arrange
        Long franchiseId = 1L;

        Franchise franchise = Franchise.builder()
                .id(franchiseId)
                .name("Test Franchise")
                .build();

        Branch branch = Branch.builder()
                .id(10L)
                .name("Single Branch")
                .franchiseId(franchiseId)
                .build();

        Product product = Product.builder()
                .id(100L)
                .name("Top Product")
                .branchId(10L)
                .stock(1000)
                .build();

        BranchWithTopProduct branchWithProduct = BranchWithTopProduct.builder()
                .branch(branch)
                .topProduct(product)
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchRepository.findBranchesWithTopProductByFranchiseId(franchiseId))
                .thenReturn(Flux.just(branchWithProduct));

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getFranchise()).isEqualTo(franchise);
                    assertThat(result.getBranchesWithTopProducts()).hasSize(1);
                    assertThat(result.getBranchesWithTopProducts().get(0).getBranch()).isEqualTo(branch);
                    assertThat(result.getBranchesWithTopProducts().get(0).getTopProduct()).isEqualTo(product);
                })
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById(franchiseId);
        verify(branchRepository, times(1)).findBranchesWithTopProductByFranchiseId(franchiseId);
    }
}
