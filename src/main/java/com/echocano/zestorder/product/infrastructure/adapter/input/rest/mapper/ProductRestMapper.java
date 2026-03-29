package com.echocano.zestorder.product.infrastructure.adapter.input.rest.mapper;

import com.echocano.zestorder.product.domain.Category;
import com.echocano.zestorder.product.domain.CorePage;
import com.echocano.zestorder.product.domain.Product;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.assembler.ProductModelAssembler;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.config.ApiRoutes;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.CategoryResponse;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ProductRequest;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import java.util.List;

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

    default PagedModel<EntityModel<ProductResponse>> toResponsePage(
            CorePage<Product> corePage,
            ProductModelAssembler assembler) {
        List<EntityModel<ProductResponse>> entityModels = corePage.getContent().stream()
                .map(assembler::toModel)
                .toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                corePage.getPageSize(),
                corePage.getPageNumber(),
                corePage.getTotalElements(),
                corePage.getTotalPages()
        );
        PagedModel<EntityModel<ProductResponse>> pagedModel = PagedModel.of(entityModels, metadata);
        String base = ApiRoutes.Products.BASE;
        int page = corePage.getPageNumber();
        int size = corePage.getPageSize();
        pagedModel.add(Link.of(String.format("%s?page=%d&size=%d", base, page, size)).withSelfRel());
        if (!corePage.isLast()) {
            pagedModel.add(Link.of(String.format("%s?page=%d&size=%d", base, page + 1, size)).withRel("next"));
        }
        if (page > 0) {
            pagedModel.add(Link.of(String.format("%s?page=%d&size=%d", base, page - 1, size)).withRel("prev"));
        }
        return pagedModel;
    }
}
