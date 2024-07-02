package de.schaumburg.schaumbooks.data_handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.stereotype.Component;

import de.schaumburg.schaumbooks.student.Student;
import de.schaumburg.schaumbooks.student.StudentRepository;

@Component
public class CsvStudentLoader {

    private StudentRepository studentRepository;

    public CsvStudentLoader(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }

    public void readStudentDataFromCsvAndSave(String csvFilePath) throws IOException{
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Skip first line:
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String klasse = parts[0].trim();
                String nachname = parts[1].trim();
                String vorname = parts[2].trim();
                // String login = parts[3].trim();
                String email = parts[4].trim();
                this.studentRepository.save(new Student(null, vorname, nachname, klasse, email, null));
            }
        } catch (Exception e) {
            System.out.println("Could not find csv-file " + e.getMessage());
        }       
    }
    
}
