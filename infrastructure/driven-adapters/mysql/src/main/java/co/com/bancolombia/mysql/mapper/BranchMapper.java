package co.com.bancolombia.mysql.mapper;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.mysql.entity.BranchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    @Mapping(target = "productList", ignore = true)
    Branch toDomain(BranchEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BranchEntity toEntity(Branch branch);
}
