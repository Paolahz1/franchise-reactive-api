package co.com.bancolombia.api.router;

import co.com.bancolombia.api.handler.BranchHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BranchRouterTest {

    @Mock
    private BranchHandler handler;

    private BranchRouter router;

    @BeforeEach
    void setUp() {
        router = new BranchRouter();
    }

    @Test
    void shouldCreateBranchRoutes() {
    
        RouterFunction<ServerResponse> routes = router.branchRoutes(handler);

        assertThat(routes).isNotNull();
    }
}
