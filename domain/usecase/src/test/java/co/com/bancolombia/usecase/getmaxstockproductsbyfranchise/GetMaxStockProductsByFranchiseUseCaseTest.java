package co.com.bancolombia.usecase.getmaxstockproductsbyfranchise;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetMaxStockProductsByFranchiseUseCase - Unit Test")
class GetMaxStockProductsByFranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private GetMaxStockProductsByFranchiseUseCase getMaxStockProductsByFranchiseUseCase;

    @Test
    @DisplayName("Should get max stock products by franchise successfully")
    void shouldGetMaxStockProductsByFranchiseSuccessfully() {
        // Given
        Long franchiseId = 1L;
        Franchise franchise = Franchise.builder()
                .id(franchiseId)
                .name("My Franchise")
                .build();

        Branch branch1 = Branch.builder()
                .id(10L)
                .name("North Branch")
                .franchiseId(franchiseId)
                .build();

        Branch branch2 = Branch.builder()
                .id(20L)
                .name("South Branch")
                .franchiseId(franchiseId)
                .build();

        Product topProduct1 = Product.builder()
                .id(100L)
                .name("Product A")
                .stock(500)
                .branchId(10L)
                .build();

        Product topProduct2 = Product.builder()
                .id(200L)
                .name("Product B")
                .stock(800)
                .branchId(20L)
                .build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(productRepository.findMaxStockByFranchise(franchiseId))
                .thenReturn(Flux.just(topProduct1, topProduct2));
        when(branchRepository.findById(10L)).thenReturn(Mono.just(branch1));
        when(branchRepository.findById(20L)).thenReturn(Mono.just(branch2));

        // Then
        StepVerifier.create(getMaxStockProductsByFranchiseUseCase.execute(franchiseId))
                .expectNextMatches(result ->
                        result.getFranchise().getId().equals(franchiseId) &&
                        result.getBranchesWithTopProducts().size() == 2 &&
                        result.getBranchesWithTopProducts().get(0).getTopProduct().getStock() == 500 &&
                        result.getBranchesWithTopProducts().get(1).getTopProduct().getStock() == 800
                )
                .verifyComplete();
    }
}
