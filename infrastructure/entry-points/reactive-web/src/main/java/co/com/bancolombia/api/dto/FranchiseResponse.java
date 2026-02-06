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
@Schema(description = "Respuesta con los datos de una franquicia")
public class FranchiseResponse {
    
    @Schema(description = "ID único de la franquicia", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de la franquicia", example = "Starbucks")
    private String name;
}
