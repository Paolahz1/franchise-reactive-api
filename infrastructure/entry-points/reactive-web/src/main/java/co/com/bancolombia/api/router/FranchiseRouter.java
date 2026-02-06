package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.FranchiseRequest;
import co.com.bancolombia.api.dto.FranchiseResponse;
import co.com.bancolombia.api.handler.FranchiseHandler;
import io.swagger.v3.oas.annotations.Operation;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FranchiseRouter {

    private static final String FRANCHISE_PATH = "/api/franchises";

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
        )
    })
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
        return route(POST(FRANCHISE_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::createFranchise);
    }
}
