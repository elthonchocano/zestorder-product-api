package com.echocano.zestorder.product;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class ZestorderProductApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZestorderProductApiApplication.class, args);
	}

	@PostConstruct
	public void init() {
		Hooks.enableAutomaticContextPropagation();
	}

	@PostConstruct
	public void setupLogback() {
		OpenTelemetryAppender.install(GlobalOpenTelemetry.get());
	}
}
