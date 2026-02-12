package co.com.bancolombia.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para actualizar el nombre")
public class UpdateNameRequest {

    @NotBlank(message = "Name must not be empty")
    @Schema(description = "New name", example = "NewName")
    private String name;
}