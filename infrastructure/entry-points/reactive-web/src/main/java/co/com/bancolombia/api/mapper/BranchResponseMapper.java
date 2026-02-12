package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.response.BranchResponse;
import co.com.bancolombia.model.branch.Branch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchResponseMapper {

    BranchResponse toResponse(Branch branch);
}
