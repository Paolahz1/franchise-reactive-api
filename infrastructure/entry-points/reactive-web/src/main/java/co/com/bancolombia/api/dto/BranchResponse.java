package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response de una sucursal")
public class BranchResponse {

    @Schema(description = "ID único de la sucursal", example = "1")
    private Long id;

    @Schema(description = "Nombre de la sucursal", example = "Starbucks Centro")
    private String name;

    @Schema(description = "ID de la franquicia a la que pertenece", example = "1")
    private Long franchiseId;
}