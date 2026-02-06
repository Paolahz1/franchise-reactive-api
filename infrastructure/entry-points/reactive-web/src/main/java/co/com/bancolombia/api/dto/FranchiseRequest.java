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
@Schema(description = "Solicitud para crear una nueva franquicia")
public class FranchiseRequest {
    
    @Schema(description = "Nombre de la franquicia", example = "Starbucks", required = true)
    private String name;
}
