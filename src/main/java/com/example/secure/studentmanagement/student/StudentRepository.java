package com.example.secure.studentmanagement.student;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByRegistrationNumber(String registrationNumber);

    boolean existsByEmail(String email);
}