package co.com.bancolombia.mysql.mapper;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.mysql.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toDomain(ProductEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductEntity toEntity(Product product);
}
