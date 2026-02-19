package co.com.bancolombia.api.router;

import co.com.bancolombia.api.handler.FranchiseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FranchiseRouterTest {

    @Mock
    private FranchiseHandler handler;

    private FranchiseRouter router;

    @BeforeEach
    void setUp() {
        router = new FranchiseRouter();
    }

    @Test
    void shouldCreateFranchiseRoutes() {
        // When
        RouterFunction<ServerResponse> routes = router.franchiseRoutes(handler);

        // Then
        assertThat(routes).isNotNull();
    }
}
