package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.ProductRequest;
import co.com.bancolombia.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    Product toDomain(ProductRequest request);
}
