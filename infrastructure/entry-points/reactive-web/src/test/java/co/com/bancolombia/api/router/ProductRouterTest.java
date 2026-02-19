package co.com.bancolombia.api.router;

import co.com.bancolombia.api.handler.ProductHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProductRouterTest {

    @Mock
    private ProductHandler handler;

    private ProductRouter router;

    @BeforeEach
    void setUp() {
        router = new ProductRouter();
    }

    @Test
    void shouldCreateProductRoutes() {
    
        RouterFunction<ServerResponse> routes = router.productRoutes(handler);


        assertThat(routes).isNotNull();
    }
}
