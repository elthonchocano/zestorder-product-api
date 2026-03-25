package com.echocano.zestorder.product.infrastructure.adapter.input.rest.mapper;

import com.echocano.zestorder.product.domain.Category;
import com.echocano.zestorder.product.domain.Product;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.CategoryResponse;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ProductRequest;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        imports = {Category.class, CategoryResponse.class})
public interface ProductRestMapper {

    @Mapping(target = "category",
            expression = "java(new CategoryResponse(domain.getCategory().name(), domain.getCategory().slug()))")
    @Mapping(target = "slug",
            expression = "java(domain.slug())")
    ProductResponse toDto(Product domain);

    @Mapping(source = "currency", target = "currencyCode")
    @Mapping(target = "category", expression = "java(new Category(document.category()))")
    Product toDomain(ProductRequest document);
}
