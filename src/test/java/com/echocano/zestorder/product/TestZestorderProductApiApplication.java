package com.echocano.zestorder.product;

import org.springframework.boot.SpringApplication;

public class TestZestorderProductApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(ZestorderProductApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
