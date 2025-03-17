package com.rickmorty.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordDto(
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    String email,
    
    @NotBlank(message = "O código é obrigatório")
    @Size(min = 6, max = 6, message = "O código deve ter 6 caracteres")
    String code,
    
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    String password,
    
    @NotBlank(message = "A confirmação de senha é obrigatória")
    String confirmPassword
) {}
