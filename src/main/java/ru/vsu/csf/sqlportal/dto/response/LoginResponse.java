package ru.vsu.csf.sqlportal.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private String role;

    public LoginResponse(String token, Long id, String login, String firstName, String lastName, String role) {
        this.token = token;
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}
