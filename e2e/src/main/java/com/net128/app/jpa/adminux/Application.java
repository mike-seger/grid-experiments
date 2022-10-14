package com.net128.app.jpa.adminux;

import com.net128.lib.spring.jpa.csv.CsvController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Import(CsvController.class)
@Profile("!test")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
