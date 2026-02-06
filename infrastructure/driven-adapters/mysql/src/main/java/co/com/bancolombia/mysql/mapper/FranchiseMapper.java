package co.com.bancolombia.mysql.mapper;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.mysql.entity.FranchiseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {

    @Mapping(target = "branchList", ignore = true)
    Franchise toDomain(FranchiseEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FranchiseEntity toEntity(Franchise franchise);
}
