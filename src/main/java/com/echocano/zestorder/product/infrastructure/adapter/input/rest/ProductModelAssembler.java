package com.echocano.zestorder.product.infrastructure.adapter.input.rest;

import com.echocano.zestorder.product.domain.Product;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ProductResponse;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.mapper.ProductRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductModelAssembler {

    private final ProductRestMapper mapper;

    public EntityModel<ProductResponse> toModel(Product product) {
        ProductResponse dto = mapper.toDto(product);
        String id = dto.id();
        Link selfLink = Link.of(Constant.BASE_PATH + "/" + id).withSelfRel();
        Link updateLink = Link.of(Constant.BASE_PATH + "/" + id).withRel("update");
        Link deleteLink = Link.of(Constant.BASE_PATH + "/" + id).withRel("delete");
        return EntityModel.of(dto, selfLink, updateLink, deleteLink);
    }
}