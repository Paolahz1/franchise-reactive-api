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
                summary = "Crear nueva franquicia",
                description = "Crea una nueva franquicia en el sistema con el nombre proporcionado",
                tags = {"Franchises"},
                requestBody = @RequestBody(
                    required = true,
                    description = "Datos de la franquicia a crear",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = FranchiseRequest.class)
                    )
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "201",
                        description = "Franquicia creada exitosamente",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FranchiseResponse.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Solicitud inválida - campos requeridos faltantes"
                    ),
                    @ApiResponse(
                        responseCode = "409",
                        description = "Conflicto - ya existe una franquicia con ese nombre"
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
                summary = "Obtener productos con mayor stock por sucursal",
                description = "Devuelve una franquicia con todas sus sucursales y para cada sucursal el producto con mayor stock",
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
                        description = "Franquicia con sus sucursales y productos top",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FranchiseWithMaxStockProductsResponse.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "404",
                        description = "Franquicia no encontrada"
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Error interno del servidor"
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
                summary = "Actualizar nombre de franquicia",
                description = "Actualiza el nombre de una franquicia existente",
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
                    description = "Nuevo nombre de la franquicia",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UpdateNameRequest.class)
                    )
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Nombre actualizado exitosamente",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FranchiseResponse.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Nombre vac\u00edo o inv\u00e1lido"
                    ),
                    @ApiResponse(
                        responseCode = "404",
                        description = "Franquicia no encontrada"
                    ),
                    @ApiResponse(
                        responseCode = "409",
                        description = "Ya existe una franquicia con ese nombre"
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Error interno del servidor"
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
