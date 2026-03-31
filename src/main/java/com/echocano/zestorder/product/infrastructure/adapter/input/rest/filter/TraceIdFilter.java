package com.echocano.zestorder.product.infrastructure.adapter.input.rest.filter;

import io.micrometer.tracing.Tracer;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter implements WebFilter {

    private final Tracer tracer;

    public TraceIdFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .contextWrite(context -> {
                    String traceId = Optional.ofNullable(tracer.currentSpan())
                            .map(span -> span.context().traceId())
                            .orElse("none");
                    exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);
                    return context;
                });
    }
}