package de.schaumburg.schaumbooks.data_handler;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class DataLoaderConfig {

    private final CsvLoader csvLoader;

    public DataLoaderConfig(CsvLoader csvLoader) {
        this.csvLoader = csvLoader;
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            String csvFilePath = ".school-data/books_test.csv";
            try {
                csvLoader.readDataFromCsvAndSave(csvFilePath);
                System.out.println("Successfully loaded data from csv file");
            } catch (IOException e) {
                System.out.println("Error while loading data from csv file: " + e.getMessage());
            }
        };
    }
}
