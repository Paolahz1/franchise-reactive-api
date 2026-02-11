package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.response.ProductResponse;
import co.com.bancolombia.model.product.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductResponseMapper {

    ProductResponse toResponse(Product product);
}
