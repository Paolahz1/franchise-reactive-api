package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para crear una nueva sucursal")
public class BranchRequest {

    @Schema(description = "Nombre de la sucursal", example = "Starbucks Centro", required = true)
    private String name;
}