package com.echocano.zestorder.product.infrastructure.adapter.input.rest.controllers;

import com.echocano.zestorder.product.application.port.input.CreateProductInputPort;
import com.echocano.zestorder.product.application.port.input.DeleteProductInputPort;
import com.echocano.zestorder.product.application.port.input.FindProductInputPort;
import com.echocano.zestorder.product.application.port.input.UpdateProductInputPort;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.config.ApiRoutes;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.assembler.ProductModelAssembler;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ProductRequest;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ProductResponse;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.mapper.ProductRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.Products.BASE)
public class ProductController {

    private final CreateProductInputPort createProductInputPort;
    private final FindProductInputPort findProductInputPort;
    private final UpdateProductInputPort updateProductInputPort;
    private final DeleteProductInputPort deleteProductInputPort;
    private final ProductRestMapper mapper;
    private final ProductModelAssembler assembler;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EntityModel<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        return createProductInputPort
                .execute(mapper.toDomain(request))
                .map(assembler::toModel);
    }

    @GetMapping(ApiRoutes.Products.BY_ID)
    public Mono<EntityModel<ProductResponse>> getOne(@PathVariable String id) {
        return findProductInputPort.findById(id)
                .map(assembler::toModel);
    }

    @PutMapping(ApiRoutes.Products.BY_ID)
    public Mono<EntityModel<ProductResponse>> update(@PathVariable String id, @Valid @RequestBody ProductRequest request) {
        return updateProductInputPort
                .update(id, mapper.toDomain(request))
                .map(assembler::toModel);
    }

    @DeleteMapping(ApiRoutes.Products.BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return deleteProductInputPort.deleteProduct(id);
    }
}
