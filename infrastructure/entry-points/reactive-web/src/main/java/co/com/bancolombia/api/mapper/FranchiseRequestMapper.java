package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.FranchiseRequest;
import co.com.bancolombia.model.franchise.Franchise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FranchiseRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branchList", ignore = true)
    Franchise toDomain(FranchiseRequest request);
}
