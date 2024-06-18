package de.schaumburg.schaumbooks;

import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// import de.schaumburg.schaumbooks.book.BookService;
import de.schaumburg.schaumbooks.data_handler.CsvLoader;

@SpringBootApplication
public class SchaumbooksApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchaumbooksApplication.class, args);
	}
}
