package co.com.bancolombia.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new branch")
public class BranchRequest {

    @Size(min = 3, max = 100, message = "The name must have between 3 and 100 characters")
    @NotBlank(message = "The branch's name is mandatory")
    @Schema(description = "Branch name", example = "Starbucks", required = true)
    private String name;
}