package co.com.bancolombia.mysql.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.BranchWithTopProduct;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.mysql.dto.BranchWithProductDto;

@Mapper(componentModel = "spring")
public interface BranchWithProductMapper {

    @Mapping(target = "branch", expression = "java(mapBranch(dto))")
    @Mapping(target = "topProduct", expression = "java(mapProduct(dto))")
    BranchWithTopProduct toDomain(BranchWithProductDto dto);

    default Branch mapBranch(BranchWithProductDto dto) {
        return Branch.builder()
                .id(dto.getBranchId())
                .name(dto.getBranchName())
                .franchiseId(dto.getFranchiseId())
                .build();
    }

    default Product mapProduct(BranchWithProductDto dto) {
        if (dto.getProductId() == null) {
            return null;
        }
        return Product.builder()
                .id(dto.getProductId())
                .name(dto.getProductName())
                .stock(dto.getProductStock())
                .branchId(dto.getProductBranchId())
                .build();
    }
}
