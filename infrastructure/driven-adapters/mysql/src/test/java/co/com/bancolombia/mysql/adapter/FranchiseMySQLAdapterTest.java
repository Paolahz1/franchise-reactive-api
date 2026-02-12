package co.com.bancolombia.mysql.adapter;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.mysql.entity.FranchiseEntity;
import co.com.bancolombia.mysql.mapper.FranchiseMapper;
import co.com.bancolombia.mysql.repository.FranchiseR2dbcRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FranchiseMySQLAdapter - Unit Tests")
class FranchiseMySQLAdapterTest {

    @Mock
    private FranchiseR2dbcRepository r2dbcRepository;

    @Mock
    private FranchiseMapper franchiseMapper;

    @InjectMocks
    private FranchiseMySQLAdapter adapter;

    @Test
    @DisplayName("Should save franchise successfully")
    void shouldSaveFranchiseSuccessfully() {
        // Arrange
        Franchise franchise = Franchise.builder()
                .name("Test Franchise")
                .build();

        FranchiseEntity entity = new FranchiseEntity();
        entity.setName("Test Franchise");

        FranchiseEntity savedEntity = new FranchiseEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Test Franchise");

        Franchise savedFranchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        when(franchiseMapper.toEntity(franchise)).thenReturn(entity);
        when(r2dbcRepository.save(entity)).thenReturn(Mono.just(savedEntity));
        when(franchiseMapper.toDomain(savedEntity)).thenReturn(savedFranchise);

        // Act & Assert
        StepVerifier.create(adapter.save(franchise))
                .expectNext(savedFranchise)
                .verifyComplete();

        verify(franchiseMapper).toEntity(franchise);
        verify(r2dbcRepository).save(entity);
        verify(franchiseMapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("Should find franchise by ID successfully")
    void shouldFindFranchiseByIdSuccessfully() {
        // Arrange
        Long franchiseId = 1L;

        FranchiseEntity entity = new FranchiseEntity();
        entity.setId(franchiseId);
        entity.setName("Test Franchise");

        Franchise franchise = Franchise.builder()
                .id(franchiseId)
                .name("Test Franchise")
                .build();

        when(r2dbcRepository.findById(franchiseId)).thenReturn(Mono.just(entity));
        when(franchiseMapper.toDomain(entity)).thenReturn(franchise);

        // Act & Assert
        StepVerifier.create(adapter.findById(franchiseId))
                .expectNext(franchise)
                .verifyComplete();

        verify(r2dbcRepository).findById(franchiseId);
        verify(franchiseMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should return empty when franchise not found by ID")
    void shouldReturnEmptyWhenFranchiseNotFoundById() {
        // Arrange
        Long franchiseId = 999L;

        when(r2dbcRepository.findById(franchiseId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.findById(franchiseId))
                .verifyComplete();

        verify(r2dbcRepository).findById(franchiseId);
        verify(franchiseMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should find franchise by name successfully")
    void shouldFindFranchiseByNameSuccessfully() {
        // Arrange
        String name = "Test Franchise";

        FranchiseEntity entity = new FranchiseEntity();
        entity.setId(1L);
        entity.setName(name);

        Franchise franchise = Franchise.builder()
                .id(1L)
                .name(name)
                .build();

        when(r2dbcRepository.findByName(name)).thenReturn(Mono.just(entity));
        when(franchiseMapper.toDomain(entity)).thenReturn(franchise);

        // Act & Assert
        StepVerifier.create(adapter.findByName(name))
                .expectNext(franchise)
                .verifyComplete();

        verify(r2dbcRepository).findByName(name);
        verify(franchiseMapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should return empty when franchise not found by name")
    void shouldReturnEmptyWhenFranchiseNotFoundByName() {
        // Arrange
        String name = "Non-existent Franchise";

        when(r2dbcRepository.findByName(name)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.findByName(name))
                .verifyComplete();

        verify(r2dbcRepository).findByName(name);
        verify(franchiseMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should update franchise name successfully")
    void shouldUpdateFranchiseNameSuccessfully() {
        // Arrange
        Long franchiseId = 1L;
        String newName = "Updated Franchise";

        when(r2dbcRepository.updateName(franchiseId, newName)).thenReturn(Mono.just(1));

        // Act & Assert
        StepVerifier.create(adapter.updateName(franchiseId, newName))
                .verifyComplete();

        verify(r2dbcRepository).updateName(franchiseId, newName);
    }
}
