package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.ProductRequest;
import co.com.bancolombia.api.dto.ProductResponse;
import co.com.bancolombia.api.dto.UpdateStockRequest;
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
    private static final String PRODUCT_PATH = "/api/branches/{branchId}/products/{productId}";
    private static final String UPDATE_STOCK_PATH = "/api/products/{productId}/stock";

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
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/products/{productId}",
                    method = RequestMethod.DELETE,
                    operation = @Operation(
                            operationId = "removeProductFromBranch",
                            summary = "Eliminar producto de sucursal",
                            description = "Elimina un producto específico de una sucursal",
                            parameters = {
                                    @Parameter(
                                            name = "branchId", 
                                            description = "ID de la sucursal", 
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "integer", format = "int64", example = "1")
                                    ),
                                    @Parameter(
                                            name = "productId", 
                                            description = "ID del producto a eliminar", 
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "integer", format = "int64", example = "1")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "204",
                                            description = "Producto eliminado exitosamente"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Sucursal o producto no encontrado"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error al eliminar el producto - el producto no pertenece a esta sucursal"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{productId}/stock",
                    method = RequestMethod.PATCH,
                    operation = @Operation(
                            operationId = "updateProductStock",
                            summary = "Actualizar stock de producto",
                            description = "Modifica la cantidad en stock de un producto específico",
                            parameters = {
                                    @Parameter(
                                            name = "productId",
                                            description = "ID del producto a actualizar",
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "integer", format = "int64", example = "1")
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Nuevo stock del producto",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateStockRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Stock actualizado exitosamente",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Stock inválido (debe ser mayor o igual a 0)"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Producto no encontrado"
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
                .route(POST(BASE_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::addProductToBranch)
                .andRoute(DELETE(PRODUCT_PATH), handler::removeProductFromBranch)
                .andRoute(PATCH(UPDATE_STOCK_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::updateProductStock);
    }
}
