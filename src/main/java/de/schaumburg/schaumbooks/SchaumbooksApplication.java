package de.schaumburg.schaumbooks;

import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import de.schaumburg.schaumbooks.book.BookService;

@SpringBootApplication
public class SchaumbooksApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchaumbooksApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(BookService bookService){
		return args -> {
			String csvFilePath = ".school-data/books_test.csv";
			try {
				bookService.readDataFromCsvAndSave(csvFilePath);
				System.out.println("Successfully loaded data from csv file");
			} catch(IOException e){
				System.out.println("Error while loading data from csv file" + e.getMessage());
			}
		};
	}
}
