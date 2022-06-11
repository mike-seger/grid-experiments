package com.net128.app.jspreadsheet.example1;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class Controller {
	@GetMapping("/data/{delay}")
	public Mono<String> data(@PathVariable("delay") long delay) {
		return Mono.just("Hello").delayElement(Duration.ofMillis(delay));
	}

	@GetMapping("/resource/{fileName}")
	public Resource getResource(@PathVariable String fileName) {
		return new ClassPathResource(fileName);
	}
}
