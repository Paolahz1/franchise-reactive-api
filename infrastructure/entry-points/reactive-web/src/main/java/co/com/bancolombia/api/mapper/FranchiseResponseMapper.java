package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.response.FranchiseResponse;
import co.com.bancolombia.model.franchise.Franchise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseResponseMapper {

    FranchiseResponse toResponse(Franchise franchise);
}
