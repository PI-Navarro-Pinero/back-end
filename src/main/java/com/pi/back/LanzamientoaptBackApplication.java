package com.pi.back;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class LanzamientoaptBackApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(LanzamientoaptBackApplication.class)
				.run(args);
	}
}
