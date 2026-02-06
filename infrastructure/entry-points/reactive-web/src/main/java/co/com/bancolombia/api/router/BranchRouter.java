package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.BranchRequest;
import co.com.bancolombia.api.dto.BranchResponse;
import co.com.bancolombia.api.dto.UpdateNameRequest;
import co.com.bancolombia.api.handler.BranchHandler;
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
public class BranchRouter {

    private static final String BASE_PATH = "/api/franchises/{franchiseId}/branches";
    private static final String UPDATE_NAME_PATH = "/api/branches/{branchId}/name";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/branches",
                    method = RequestMethod.POST,
                    operation = @Operation(
                            operationId = "addBranchToFranchise",
                            summary = "Agregar sucursal a franquicia",
                            description = "Agrega una nueva sucursal a una franquicia existente",
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
                                    description = "Datos de la nueva sucursal",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = BranchRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Sucursal creada exitosamente",
                                            content = @Content(schema = @Schema(implementation = BranchResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Franquicia no encontrada o datos inválidos"
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "Ya existe una sucursal con ese nombre en la franquicia"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/name",
                    method = RequestMethod.PATCH,
                    operation = @Operation(
                            operationId = "updateBranchName",
                            summary = "Actualizar nombre de sucursal",
                            description = "Actualiza el nombre de una sucursal existente",
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
                                    description = "Nuevo nombre de la sucursal",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Nombre actualizado exitosamente",
                                            content = @Content(schema = @Schema(implementation = BranchResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Nombre vacío o inválido"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Sucursal no encontrada"
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "Ya existe una sucursal con ese nombre en la franquicia"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor"
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> branchRoutes(BranchHandler handler) {
        return RouterFunctions
                .route(POST(BASE_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::addBranchToFranchise)
                .andRoute(PATCH(UPDATE_NAME_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::updateBranchName);
    }
}