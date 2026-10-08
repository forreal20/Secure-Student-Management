package com.example.secure.studentmanagement.student;

import com.example.secure.studentmanagement.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {

        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + id
                        ));
    }

    public Student updateStudent(Long id, Student updatedStudent) {

        Student student = getStudentById(id);

        student.setRegistrationNumber(
                updatedStudent.getRegistrationNumber()
        );

        student.setFullName(
                updatedStudent.getFullName()
        );

        student.setEmail(
                updatedStudent.getEmail()
        );

        student.setPhone(
                updatedStudent.getPhone()
        );

        student.setGender(
                updatedStudent.getGender()
        );

        student.setDateOfBirth(
                updatedStudent.getDateOfBirth()
        );

        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {

        Student student = getStudentById(id);

        studentRepository.delete(student);
    }
}