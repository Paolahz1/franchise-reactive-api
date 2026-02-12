package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.response.BranchWithTopProductResponse;
import co.com.bancolombia.api.dto.response.FranchiseWithMaxStockProductsResponse;
import co.com.bancolombia.api.dto.response.TopProductResponse;
import co.com.bancolombia.model.branch.BranchWithTopProduct;
import co.com.bancolombia.model.franchise.FranchiseWithTopProducts;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface FranchiseWithMaxStockProductsResponseMapper {

    @Mapping(target = "franchiseId", source = "franchise.id")
    @Mapping(target = "franchiseName", source = "franchise.name")
    @Mapping(target = "branches", expression = "java(mapBranches(domain.getBranchesWithTopProducts()))")
    FranchiseWithMaxStockProductsResponse toResponse(FranchiseWithTopProducts domain);

    default List<BranchWithTopProductResponse> mapBranches(List<BranchWithTopProduct> branchesWithTopProducts) {
        return branchesWithTopProducts.stream()
                .map(this::mapBranchWithTopProduct)
                .collect(Collectors.toList());
    }

    default BranchWithTopProductResponse mapBranchWithTopProduct(BranchWithTopProduct branchWithProduct) {
        return BranchWithTopProductResponse.builder()
                .branchId(branchWithProduct.getBranch().getId())
                .branchName(branchWithProduct.getBranch().getName())
                .topProduct(mapTopProduct(branchWithProduct))
                .build();
    }

    default TopProductResponse mapTopProduct(BranchWithTopProduct branchWithProduct) {
        if (branchWithProduct.getTopProduct() == null) {
            return null;
        }
        return TopProductResponse.builder()
                .productId(branchWithProduct.getTopProduct().getId())
                .productName(branchWithProduct.getTopProduct().getName())
                .stock(branchWithProduct.getTopProduct().getStock())
                .build();
    }
}
