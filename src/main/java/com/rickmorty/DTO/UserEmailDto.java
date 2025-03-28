package com.rickmorty.DTO;

import jakarta.validation.constraints.Email;

public record UserEmailDto(
    @Email(message = "{email.Email}") 
    String email
) {

}
