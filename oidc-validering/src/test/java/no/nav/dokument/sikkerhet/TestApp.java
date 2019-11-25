package no.nav.dokument.sikkerhet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple Spring Boot app to test Spring configuration isolated from
 * integrasjonspunkt.
 */
@SpringBootApplication
@RestController
public class TestApp {

	public static void main(String[] args) {
		SpringApplication.run(TestApp.class, args);
	}

	@RequestMapping(path = "/api")
	public String api() {
		return "api";
	}

	@RequestMapping(path = "/unprotected")
	public String unprotected() {
		return "public";
	}
}

