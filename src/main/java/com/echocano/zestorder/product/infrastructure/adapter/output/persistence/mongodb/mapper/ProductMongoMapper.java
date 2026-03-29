package com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb.mapper;

import com.echocano.zestorder.product.domain.Category;
import com.echocano.zestorder.product.domain.CustomizationGroup;
import com.echocano.zestorder.product.domain.CustomizationOption;
import com.echocano.zestorder.product.domain.Product;
import com.echocano.zestorder.product.domain.ProductAttribute;
import com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb.entities.CategoryDocument;
import com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb.entities.CustomizationGroupDocument;
import com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb.entities.CustomizationOptionDocument;
import com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb.entities.ProductAttributeDocument;
import com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb.entities.ProductDocument;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring"
        , builder = @org.mapstruct.Builder(disableBuilder = false))
public interface ProductMongoMapper {

    Product toDomain(ProductDocument document);

    List<Product> toDomains(List<ProductDocument> document);

    ProductDocument toDocument(Product domain);

    Category toDomain(CategoryDocument document);

    CategoryDocument toDocument(Category domain);

    @Mapping(source = "available", target = "isAvailable")
    ProductAttribute toDomain(ProductAttributeDocument document);

    @InheritInverseConfiguration
    ProductAttributeDocument toDocument(ProductAttribute domain);

    @Mapping(source = "required", target = "isRequired")
    CustomizationGroup toDomain(CustomizationGroupDocument document);

    @InheritInverseConfiguration
    CustomizationGroupDocument toDocument(CustomizationGroup domain);

    CustomizationOption toDomain(CustomizationOptionDocument document);

    CustomizationOptionDocument toDocument(CustomizationOption domain);
}
