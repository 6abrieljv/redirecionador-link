package com.redirecionador.redirecionador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RedirecionadorLinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedirecionadorLinkApplication.class, args);
	}

}
