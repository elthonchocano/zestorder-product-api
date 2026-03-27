package com.echocano.zestorder.product.infrastructure.adapter.output.messaging.kafka.mapper;

import com.echocano.zestorder.product.domain.Product;
import com.echocano.zestorder.product.infrastructure.adapter.output.kafka.events.ProductCreated;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductKafkaMapper {

    @Mapping(target = "slug", expression = "java(domain.slug())")
    @Mapping(target = "category", expression = "java(domain.getCategory().name())")
    @Mapping(target = "timestamp", expression = "java(domain.getCreatedAt().toEpochMilli())")
    ProductCreated toEvent(Product domain);
}
