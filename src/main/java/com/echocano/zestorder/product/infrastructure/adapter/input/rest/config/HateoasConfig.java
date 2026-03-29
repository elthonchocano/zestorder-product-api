package com.echocano.zestorder.product.infrastructure.adapter.input.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.DelegatingLinkRelationProvider;
import org.springframework.plugin.core.OrderAwarePluginRegistry;

import java.util.List;

@Configuration
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class HateoasConfig {

    @Bean
    public LinkRelationProvider linkRelationProvider() {
        return new DelegatingLinkRelationProvider(
                OrderAwarePluginRegistry.of(List.of())
        );
    }
}
