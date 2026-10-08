package com.example.secure.studentmanagement.user;

public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String role;

    public UserResponse() {
    }

    public UserResponse(
            Long id,
            String username,
            String email,
            String role) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public static UserResponse fromEntity(User user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}