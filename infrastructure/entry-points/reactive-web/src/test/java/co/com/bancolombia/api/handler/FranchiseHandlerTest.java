package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.request.FranchiseRequest;
import co.com.bancolombia.api.dto.request.UpdateNameRequest;
import co.com.bancolombia.api.dto.response.FranchiseResponse;
import co.com.bancolombia.api.dto.response.FranchiseWithMaxStockProductsResponse;
import co.com.bancolombia.api.mapper.FranchiseRequestMapper;
import co.com.bancolombia.api.mapper.FranchiseResponseMapper;
import co.com.bancolombia.api.mapper.FranchiseWithMaxStockProductsResponseMapper;
import co.com.bancolombia.api.utils.LoggingUtils;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.FranchiseWithTopProducts;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.getmaxstockproductsbyfranchise.GetMaxStockProductsByFranchiseUseCase;
import co.com.bancolombia.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FranchiseHandler - Unit Tests")
class FranchiseHandlerTest {

    @Mock
    private CreateFranchiseUseCase createFranchiseUseCase;

    @Mock
    private GetMaxStockProductsByFranchiseUseCase getMaxStockProductsByFranchiseUseCase;

    @Mock
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    @Mock
    private FranchiseRequestMapper franchiseRequestMapper;

    @Mock
    private FranchiseResponseMapper franchiseResponseMapper;

    @Mock
    private FranchiseWithMaxStockProductsResponseMapper franchiseWithMaxStockProductsResponseMapper;

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private FranchiseHandler handler;

    @Test
    @DisplayName("Should create franchise successfully")
    void shouldCreateFranchiseSuccessfully() {
        // Arrange
        FranchiseRequest request = new FranchiseRequest();
        request.setName("Test Franchise");

        Franchise franchise = Franchise.builder()
                .name("Test Franchise")
                .build();

        Franchise savedFranchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        FranchiseResponse response = new FranchiseResponse();
        response.setId(1L);
        response.setName("Test Franchise");

        when(serverRequest.bodyToMono(FranchiseRequest.class)).thenReturn(Mono.just(request));
        when(franchiseRequestMapper.toDomain(request)).thenReturn(franchise);
        when(createFranchiseUseCase.execute(franchise)).thenReturn(Mono.just(savedFranchise));
        when(franchiseResponseMapper.toResponse(savedFranchise)).thenReturn(response);

        // Act & Assert
        StepVerifier.create(handler.createFranchise(serverRequest))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();

        verify(loggingUtils).logRequest("CREATE_FRANCHISE", serverRequest);
        verify(createFranchiseUseCase).execute(franchise);
    }

    @Test
    @DisplayName("Should return error when request body is empty")
    void shouldReturnErrorWhenRequestBodyIsEmpty() {
        // Arrange
        when(serverRequest.bodyToMono(FranchiseRequest.class)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(handler.createFranchise(serverRequest))
                .expectErrorMatches(throwable ->
                    throwable instanceof BusinessException &&
                    ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.REQUIRED_FIELD_MISSING
                )
                .verify();

        verify(loggingUtils).logRequest("CREATE_FRANCHISE", serverRequest);
    }

    @Test
    @DisplayName("Should get max stock products successfully")
    void shouldGetMaxStockProductsSuccessfully() {
        // Arrange
        Long franchiseId = 1L;
        
        FranchiseWithTopProducts franchiseWithProducts = FranchiseWithTopProducts.builder()
                .franchise(Franchise.builder().id(franchiseId).name("Test").build())
                .branchesWithTopProducts(Collections.emptyList())
                .build();

        FranchiseWithMaxStockProductsResponse response = new FranchiseWithMaxStockProductsResponse();

        when(serverRequest.pathVariable("franchiseId")).thenReturn(String.valueOf(franchiseId));
        when(getMaxStockProductsByFranchiseUseCase.execute(franchiseId)).thenReturn(Mono.just(franchiseWithProducts));
        when(franchiseWithMaxStockProductsResponseMapper.toResponse(franchiseWithProducts)).thenReturn(response);

        // Act & Assert
        StepVerifier.create(handler.getMaxStockProducts(serverRequest))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.OK
                )
                .verifyComplete();

        verify(loggingUtils).logRequest("GET_MAX_STOCK_PRODUCTS", serverRequest);
        verify(getMaxStockProductsByFranchiseUseCase).execute(franchiseId);
    }

    @Test
    @DisplayName("Should update franchise name successfully")
    void shouldUpdateFranchiseNameSuccessfully() {
        // Arrange
        Long franchiseId = 1L;
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Updated Franchise");

        Franchise updatedFranchise = Franchise.builder()
                .id(franchiseId)
                .name("Updated Franchise")
                .build();

        FranchiseResponse response = new FranchiseResponse();
        response.setId(franchiseId);
        response.setName("Updated Franchise");

        when(serverRequest.pathVariable("franchiseId")).thenReturn(String.valueOf(franchiseId));
        when(serverRequest.bodyToMono(UpdateNameRequest.class)).thenReturn(Mono.just(request));
        when(updateFranchiseNameUseCase.execute(franchiseId, "Updated Franchise")).thenReturn(Mono.just(updatedFranchise));
        when(franchiseResponseMapper.toResponse(updatedFranchise)).thenReturn(response);

        // Act & Assert
        StepVerifier.create(handler.updateFranchiseName(serverRequest))
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.OK
                )
                .verifyComplete();

        verify(loggingUtils).logRequest("UPDATE_FRANCHISE_NAME", serverRequest);
        verify(updateFranchiseNameUseCase).execute(franchiseId, "Updated Franchise");
    }
}
