package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.request.ProductRequest;
import co.com.bancolombia.api.dto.response.ProductResponse;
import co.com.bancolombia.api.dto.request.UpdateNameRequest;
import co.com.bancolombia.api.dto.request.UpdateStockRequest;
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
    private static final String UPDATE_PRODUCT_NAME_PATH = "/api/products/{productId}/name";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/branches/{branchId}/products",
                    method = RequestMethod.POST,
                    operation = @Operation(
                            operationId = "addProductToBranch",
                            summary = "Add product to branch",
                            description = "Adds a new product to a specific branch with initial stock",
                            parameters = {
                                    @Parameter(
                                            name = "branchId", 
                                            description = "Branch ID", 
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "integer", format = "int64", example = "1")
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "New product data",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Product added successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid data or branch not found"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Branch not found"
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "A product with that name already exists in the branch"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/products/{productId}",
                    method = RequestMethod.DELETE,
                    operation = @Operation(
                            operationId = "removeProductFromBranch",
                            summary = "Remove product from branch",
                            description = "Removes a specific product from a branch",
                            parameters = {
                                    @Parameter(
                                            name = "branchId", 
                                            description = "Branch ID", 
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "integer", format = "int64", example = "1")
                                    ),
                                    @Parameter(
                                            name = "productId", 
                                            description = "Product ID to remove", 
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "integer", format = "int64", example = "1")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "204",
                                            description = "Product removed successfully"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Branch or product not found"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error removing product - product does not belong to this branch"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{productId}/stock",
                    method = RequestMethod.PATCH,
                    operation = @Operation(
                            operationId = "updateProductStock",
                            summary = "Update product stock",
                            description = "Modifies the stock quantity of a specific product",
                            parameters = {
                                    @Parameter(
                                            name = "productId",
                                            description = "Product ID to update",
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "integer", format = "int64", example = "1")
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "New product stock",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateStockRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Stock updated successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid stock (must be greater than or equal to 0)"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Product not found"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{productId}/name",
                    method = RequestMethod.PATCH,
                    operation = @Operation(
                            operationId = "updateProductName",
                            summary = "Update product name",
                            description = "Updates the name of an existing product",
                            parameters = {
                                    @Parameter(
                                            name = "productId",
                                            description = "Product ID to update",
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "integer", format = "int64", example = "1")
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "New product name",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Name updated successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Empty or invalid name"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Product not found"
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "A product with that name already exists in the branch"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error"
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
        return RouterFunctions
                .route(POST(BASE_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::addProductToBranch)
                .andRoute(DELETE(PRODUCT_PATH), handler::removeProductFromBranch)
                .andRoute(PATCH(UPDATE_STOCK_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::updateProductStock)
                .andRoute(PATCH(UPDATE_PRODUCT_NAME_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::updateProductName);
    }
}
