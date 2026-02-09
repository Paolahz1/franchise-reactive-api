package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.FranchiseRequest;
import co.com.bancolombia.api.dto.FranchiseResponse;
import co.com.bancolombia.api.dto.FranchiseWithMaxStockProductsResponse;
import co.com.bancolombia.api.dto.UpdateNameRequest;
import co.com.bancolombia.api.handler.FranchiseHandler;
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
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FranchiseRouter {

    private static final String FRANCHISE_PATH = "/api/franchises";
    private static final String MAX_STOCK_PATH = "/api/franchises/{franchiseId}/max-stock-products";
    private static final String UPDATE_NAME_PATH = "/api/franchises/{franchiseId}/name";

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/franchises",
            method = RequestMethod.POST,
            beanClass = FranchiseHandler.class,
            beanMethod = "createFranchise",
            operation = @Operation(
                operationId = "createFranchise",
                summary = "Create new franchise",
                description = "Creates a new franchise in the system with the provided name",
                tags = {"Franchises"},
                requestBody = @RequestBody(
                    required = true,
                    description = "Franchise data to create",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = FranchiseRequest.class)
                    )
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "201",
                        description = "Franchise created successfully",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FranchiseResponse.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request - required fields missing"
                    ),
                    @ApiResponse(
                        responseCode = "409",
                        description = "Conflict - a franchise with that name already exists"
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Error interno del servidor"
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/franchises/{franchiseId}/max-stock-products",
            method = RequestMethod.GET,
            beanClass = FranchiseHandler.class,
            beanMethod = "getMaxStockProducts",
            operation = @Operation(
                operationId = "getMaxStockProducts",
                summary = "Get products with highest stock per branch",
                description = "Returns a franchise with all its branches and for each branch the product with highest stock",
                tags = {"Franchises"},
                parameters = {
                    @Parameter(
                        name = "franchiseId",
                        description = "ID de la franquicia",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "integer", format = "int64", example = "1")
                    )
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Franchise with its branches and top products",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FranchiseWithMaxStockProductsResponse.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "404",
                        description = "Franchise not found"
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error"
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/franchises/{franchiseId}/name",
            method = RequestMethod.PATCH,
            beanClass = FranchiseHandler.class,
            beanMethod = "updateFranchiseName",
            operation = @Operation(
                operationId = "updateFranchiseName",
                summary = "Update franchise name",
                description = "Updates the name of an existing franchise",
                tags = {"Franchises"},
                parameters = {
                    @Parameter(
                        name = "franchiseId",
                        description = "ID de la franquicia",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(type = "integer", format = "int64", example = "1")
                    )
                },
                requestBody = @RequestBody(
                    required = true,
                    description = "New franchise name",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UpdateNameRequest.class)
                    )
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Name updated successfully",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FranchiseResponse.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Empty or invalid name"
                    ),
                    @ApiResponse(
                        responseCode = "404",
                        description = "Franchise not found"
                    ),
                    @ApiResponse(
                        responseCode = "409",
                        description = "A franchise with that name already exists"
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error"
                    )
                }
            )
        )
    })
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
        return route(POST(FRANCHISE_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::createFranchise)
                .andRoute(GET(MAX_STOCK_PATH), handler::getMaxStockProducts)
                .andRoute(PATCH(UPDATE_NAME_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::updateFranchiseName);
    }
}
