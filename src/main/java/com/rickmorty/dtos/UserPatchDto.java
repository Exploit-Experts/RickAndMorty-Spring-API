package com.rickmorty.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserPatchDto(
        @Size(min = 3, max = 50, message = "{name.Size}")
        String name,

        @Size(min = 3, max = 50, message = "{surname.Size}")
        String surname,

        @Email(message = "{email.Email}")
        String email,

        @Size(min = 6, message = "{password.Size}")
        String password
) {
}
