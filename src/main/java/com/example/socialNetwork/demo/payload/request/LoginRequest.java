package com.example.socialNetwork.demo.payload.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * DTO object for Login page
 */
@Data
public class LoginRequest {

    @NotEmpty(message = "Username cannot be empty")
    private String username;
    @NotEmpty(message = "Password cannot be empty")
    private String password;
}
