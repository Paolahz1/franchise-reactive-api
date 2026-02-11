package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.request.BranchRequest;
import co.com.bancolombia.api.dto.response.BranchResponse;
import co.com.bancolombia.api.dto.request.UpdateNameRequest;
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
                            summary = "Add branch to franchise",
                            description = "Adds a new branch to an existing franchise",
                            parameters = {
                                    @Parameter(
                                            name = "franchiseId", 
                                            description = "Franchise ID", 
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "integer", format = "int64", example = "1")
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "New branch data",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = BranchRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Branch created successfully",
                                            content = @Content(schema = @Schema(implementation = BranchResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Franchise not found or invalid data"
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "A branch with that name already exists in the franchise"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/name",
                    method = RequestMethod.PATCH,
                    operation = @Operation(
                            operationId = "updateBranchName",
                            summary = "Update branch name",
                            description = "Updates the name of an existing branch",
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
                                    description = "New branch name",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Name updated successfully",
                                            content = @Content(schema = @Schema(implementation = BranchResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Empty or invalid name"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Branch not found"
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "A branch with that name already exists in the franchise"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error"
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