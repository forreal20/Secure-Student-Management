package com.example.secure.studentmanagement.student;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {

        return ResponseEntity.ok(
                studentService.getAllStudents()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                studentService.getStudentById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(
            @RequestBody Student student) {

        return ResponseEntity.ok(
                studentService.createStudent(student)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id,
            @RequestBody Student student) {

        return ResponseEntity.ok(
                studentService.updateStudent(id, student)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(
            @PathVariable Long id) {

        studentService.deleteStudent(id);

        return ResponseEntity.ok(
                "Student deleted successfully"
        );
    }
}