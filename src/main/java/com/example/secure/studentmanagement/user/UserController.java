package com.example.secure.studentmanagement.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET ALL USERS
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users =
                userRepository.findAll()
                        .stream()
                        .map(UserResponse::fromEntity)
                        .toList();

        return ResponseEntity.ok(users);
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new org.springframework.web.server
                                .ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "User not found with id: " + id
                        ));

        return ResponseEntity.ok(
                UserResponse.fromEntity(user)
        );
    }
}