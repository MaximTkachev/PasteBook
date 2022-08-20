package com.qwerty.pastebook.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    @Size(min = 1, max = 255, message = "invalid username")
    private String username;

    @Size(min = 8, max = 255, message = "invalid password")
    private String password;
}
