package co.com.bancolombia.mysql.adapter;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.BranchWithTopProduct;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.mysql.dto.BranchWithProductDto;
import co.com.bancolombia.mysql.entity.BranchEntity;
import co.com.bancolombia.mysql.mapper.BranchMapper;
import co.com.bancolombia.mysql.mapper.BranchWithProductMapper;
import co.com.bancolombia.mysql.repository.BranchR2dbcRepository;
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
@DisplayName("BranchMySQLAdapter - Unit Tests")
class BranchMySQLAdapterTest {

    @Mock
    private BranchR2dbcRepository r2dbcRepository;

    @Mock
    private BranchMapper branchMapper;

    @Mock
    private BranchWithProductMapper branchWithProductMapper;

    @InjectMocks
    private BranchMySQLAdapter adapter;

    @Test
    @DisplayName("Should save branch successfully")
    void shouldSaveBranchSuccessfully() {
        // Arrange
        Branch branch = Branch.builder()
                .name("Test Branch")
                .franchiseId(1L)
                .build();

        BranchEntity entity = new BranchEntity();
        entity.setName("Test Branch");
        entity.setFranchiseId(1L);

        BranchEntity savedEntity = new BranchEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Test Branch");
        savedEntity.setFranchiseId(1L);

        Branch savedBranch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();

        when(branchMapper.toEntity(branch)).thenReturn(entity);
        when(r2dbcRepository.save(entity)).thenReturn(Mono.just(savedEntity));
        when(branchMapper.toDomain(savedEntity)).thenReturn(savedBranch);

        // Act & Assert
        StepVerifier.create(adapter.save(branch))
                .expectNext(savedBranch)
                .verifyComplete();

        verify(branchMapper).toEntity(branch);
        verify(r2dbcRepository).save(entity);
        verify(branchMapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("Should find branch by ID successfully")
    void shouldFindBranchByIdSuccessfully() {
        // Arrange
        Long branchId = 1L;

        BranchEntity entity = new BranchEntity();
        entity.setId(branchId);
        entity.setName("Test Branch");
        entity.setFranchiseId(1L);

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Test Branch")
                .franchiseId(1L)
                .build();

        when(r2dbcRepository.findById(branchId)).thenReturn(Mono.just(entity));
        when(branchMapper.toDomain(entity)).thenReturn(branch);

        // Act & Assert
        StepVerifier.create(adapter.findById(branchId))
                .expectNext(branch)
                .verifyComplete();

        verify(r2dbcRepository).findById(branchId);
        verify(branchMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should find branch by name and franchise ID successfully")
    void shouldFindBranchByNameAndFranchiseIdSuccessfully() {
        // Arrange
        String name = "Test Branch";
        Long franchiseId = 1L;

        BranchEntity entity = new BranchEntity();
        entity.setId(1L);
        entity.setName(name);
        entity.setFranchiseId(franchiseId);

        Branch branch = Branch.builder()
                .id(1L)
                .name(name)
                .franchiseId(franchiseId)
                .build();

        when(r2dbcRepository.findByNameAndFranchiseId(name, franchiseId)).thenReturn(Mono.just(entity));
        when(branchMapper.toDomain(entity)).thenReturn(branch);

        // Act & Assert
        StepVerifier.create(adapter.findByNameAndFranchiseId(name, franchiseId))
                .expectNext(branch)
                .verifyComplete();

        verify(r2dbcRepository).findByNameAndFranchiseId(name, franchiseId);
        verify(branchMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should update branch name successfully")
    void shouldUpdateBranchNameSuccessfully() {
        // Arrange
        Long branchId = 1L;
        String newName = "Updated Branch";

        when(r2dbcRepository.updateName(branchId, newName)).thenReturn(Mono.just(1));

        // Act & Assert
        StepVerifier.create(adapter.updateName(branchId, newName))
                .verifyComplete();

        verify(r2dbcRepository).updateName(branchId, newName);
    }

    @Test
    @DisplayName("Should find branches with top product by franchise ID successfully")
    void shouldFindBranchesWithTopProductByFranchiseIdSuccessfully() {
        // Arrange
        Long franchiseId = 1L;

        BranchWithProductDto dto1 = new BranchWithProductDto();
        BranchWithProductDto dto2 = new BranchWithProductDto();

        Branch branch1 = Branch.builder().id(1L).name("Branch 1").franchiseId(franchiseId).build();
        Product product1 = Product.builder().id(10L).name("Product 1").stock(100).branchId(1L).build();
        BranchWithTopProduct branchWithProduct1 = BranchWithTopProduct.builder()
                .branch(branch1)
                .topProduct(product1)
                .build();

        Branch branch2 = Branch.builder().id(2L).name("Branch 2").franchiseId(franchiseId).build();
        Product product2 = Product.builder().id(20L).name("Product 2").stock(200).branchId(2L).build();
        BranchWithTopProduct branchWithProduct2 = BranchWithTopProduct.builder()
                .branch(branch2)
                .topProduct(product2)
                .build();

        when(r2dbcRepository.findBranchesWithTopProductByFranchiseId(franchiseId))
                .thenReturn(Flux.just(dto1, dto2));
        when(branchWithProductMapper.toDomain(dto1)).thenReturn(branchWithProduct1);
        when(branchWithProductMapper.toDomain(dto2)).thenReturn(branchWithProduct2);

        // Act & Assert
        StepVerifier.create(adapter.findBranchesWithTopProductByFranchiseId(franchiseId))
                .expectNext(branchWithProduct1)
                .expectNext(branchWithProduct2)
                .verifyComplete();

        verify(r2dbcRepository).findBranchesWithTopProductByFranchiseId(franchiseId);
        verify(branchWithProductMapper, times(2)).toDomain(any());
    }
}
