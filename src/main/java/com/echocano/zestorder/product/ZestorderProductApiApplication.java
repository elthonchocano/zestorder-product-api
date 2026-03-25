package com.echocano.zestorder.product;

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
}
