package com.qwerty.pastebook.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {

    @Size(max = 255,
            message = "username length cannot be more than 255 symbols")
    @NotBlank(message = "username cannot be blank")
    private String username;

    @Size(min = 8, max = 255,
            message = "password length cannot be less than 5 and more than 255 symbols")
    private String password;
}
