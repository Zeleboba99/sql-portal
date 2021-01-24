package ru.vsu.csf.sqlportal.dto.request;

import lombok.Data;

@Data
public class SignupRequest {
    private String firstName;
    private String lastName;
    private String login;
    private String password;
    private String role;
}
