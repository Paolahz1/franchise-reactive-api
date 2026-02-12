package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.BranchRequest;
import co.com.bancolombia.model.branch.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "franchiseId", ignore = true)
    @Mapping(target = "productList", ignore = true)
    Branch toDomain(BranchRequest request);
}
