package de.schaumburg.schaumbooks.data_handler;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class DataLoaderConfig {

    private final CsvBookLoader csvBookLoader;
    private final CsvStudentLoader csvStudentLoader;

    public DataLoaderConfig(CsvBookLoader csvLoader, CsvStudentLoader csvStudentLoader) {
        this.csvBookLoader = csvLoader;
        this.csvStudentLoader = csvStudentLoader;
    }

    @Bean
    CommandLineRunner initBooksData() {
        return args -> {
            String csvFilePath = ".school-data/books_test.csv";
            try {
                csvBookLoader.readBookDataFromCsvAndSave(csvFilePath);
                System.out.println("Successfully loaded book data from csv file");
            } catch (IOException e) {
                System.out.println("Error while loading data from csv file: " + e.getMessage());
            }
        };
    }

    @Bean
    CommandLineRunner initStudentsData() {
        return args -> {
            String csvFilePath = ".school-data/schuelerliste_test.csv";
            try {
                csvStudentLoader.readStudentDataFromCsvAndSave(csvFilePath);
                System.out.println("Successfully loaded students data from csv file");
            } catch (IOException e) {
                System.out.println("Error while loading data from csv file: " + e.getMessage());
            }
        };
    }
}
