package com.echocano.zestorder.product.infrastructure.adapter.output.mongodb.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "unique_prod_in_cat", def = "{'name': 1, 'category.name': 1}", unique = true)
public class ProductDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String sku;

    private String name;
    private String description;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal basePrice;

    private String currencyCode;
    private CategoryDocument category; // Embedded object
    private ProductAttributeDocument attribute;
    private List<CustomizationGroupDocument> customizations;
    private Set<String> tags;
    private List<String> imageUrls;
    private String status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
