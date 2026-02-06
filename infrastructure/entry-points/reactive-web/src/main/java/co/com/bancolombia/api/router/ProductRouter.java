package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.ProductRequest;
import co.com.bancolombia.api.dto.ProductResponse;
import co.com.bancolombia.api.handler.ProductHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ProductRouter {

    private static final String BASE_PATH = "/api/branches/{branchId}/products";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/branches/{branchId}/products",
                    method = RequestMethod.POST,
                    operation = @Operation(
                            operationId = "addProductToBranch",
                            summary = "Agregar producto a sucursal",
                            description = "Agrega un nuevo producto a una sucursal específica con stock inicial",
                            parameters = {
                                    @Parameter(
                                            name = "branchId", 
                                            description = "ID de la sucursal", 
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "integer", format = "int64", example = "1")
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Datos del nuevo producto",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Producto agregado exitosamente",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos inválidos o sucursal no encontrada"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Sucursal no encontrada"
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "Ya existe un producto con ese nombre en la sucursal"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor"
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
        return RouterFunctions
                .route(POST(BASE_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::addProductToBranch);
    }
}
