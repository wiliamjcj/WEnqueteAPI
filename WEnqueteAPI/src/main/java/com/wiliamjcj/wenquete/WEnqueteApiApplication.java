package com.wiliamjcj.wenquete;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport
@SpringBootApplication
public class WEnqueteApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WEnqueteApiApplication.class, args);
	}
}
