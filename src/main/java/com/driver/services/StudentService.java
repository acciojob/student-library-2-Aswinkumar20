package com.driver.services;

import com.driver.models.Card;
import com.driver.models.CardStatus;
import com.driver.models.Student;
import com.driver.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.driver.models.CardStatus.ACTIVATED;

@Service
public class StudentService {

    @Autowired
    CardService cardService4;

    @Autowired
    StudentRepository studentRepository4;


    public Student getDetailsByEmail(String email){
        Student student = studentRepository4.findByEmailId(email);

        return student;
    }

    public Student getDetailsById(int id){
        Student student = studentRepository4.findById(id);

        return student;
    }

    public void createStudent(Student student){


        Card card1 = Card.builder()
                .student(student)
                .cardStatus(ACTIVATED)
                .build();


        studentRepository4.save(student);

    }

    public void updateStudent(Student student){
        studentRepository4.updateStudentDetails(student);

    }

    public void deleteStudent(int id){
        //Delete student and deactivate corresponding card
        studentRepository4.deleteCustom(id);
    }
}
