package com.pi.back;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class PinpApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(PinpApplication.class)
				.run(args);
	}
}
