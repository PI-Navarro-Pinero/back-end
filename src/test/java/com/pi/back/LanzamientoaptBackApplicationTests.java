package com.pi.back;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class LanzamientoaptBackApplicationTests {

	@Autowired
	private LanzamientoaptBackApplication app;

	@Test
	void contextLoads() {
		assertNotNull(app);
	}

}
