package com.alvarobf0.similarproducts;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class SimilarproductsApplicationTests {

	@Test
	void whenTestApplicationStarts_thenContextLoadsSuccessfully(ApplicationContext context) {
		assertNotNull(context);
	}
}
