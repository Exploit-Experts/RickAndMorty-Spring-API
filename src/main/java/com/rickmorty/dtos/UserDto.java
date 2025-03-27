package com.rickmorty.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDto(
        @NotBlank(message = "{name.NotBlank}")
        @Size(min = 3, max = 50, message = "{name.Size}")
        String name,

        @NotBlank(message = "{surname.NotBlank}")
        @Size(min = 3, max = 50, message = "{surname.Size}")
        String surname,

        @NotBlank(message = "{email.NotBlank}")
        @Email(message = "{email.Email}")
        String email,

        @NotBlank(message = "{password.NotBlank}")
        @Size(min = 6, message = "{password.Size}")
        String password
) {}
