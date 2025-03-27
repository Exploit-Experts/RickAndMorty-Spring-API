package com.rickmorty.controllers;

import com.rickmorty.dtos.AuthenticationDto;
import com.rickmorty.dtos.UserDto;
import com.rickmorty.dtos.UserEmailDto;
import com.rickmorty.dtos.ResetPasswordDto;
import com.rickmorty.services.AuthService;
import com.rickmorty.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/${api.version}/auth")
public class AuthenticationController {

        private final AuthService authService;  
        private final EmailService emailService;

        public AuthenticationController(AuthService authService, EmailService emailService) {
                this.authService = authService;
                this.emailService = emailService;
        }

        @Operation(summary = "Create a new user", description = "Create a new user with the provided details", responses = {
                        @ApiResponse(responseCode = "201", description = "User created"),
                        @ApiResponse(responseCode = "400", description = "Incorrect data submission", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(name = "Invalid name", value = "{\"errors\": [\"O nome é obrigatório\", \"O nome deve ter entre 3 e 50 caracteres.\"]}"),
                                        @ExampleObject(name = "Invalid surname", value = "{\"errors\": [\"O sobrenome é obrigatório\", \"O sobrenome deve ter entre 3 e 50 caracteres.\"]}"),
                                        @ExampleObject(name = "Invalid email", value = "{\"errors\": [\"O e-mail é obrigatório\", \"Formato de e-mail inválido.\"]}"),
                                        @ExampleObject(name = "Invalid password", value = "{\"errors\": [\"A senha é obrigatória\", \"A senha deve ter pelo menos 6 caracteres.\"]}")
                        })),
                        @ApiResponse(responseCode = "401", description = "User not authenticate", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(name = "Authentication requered", value = "{\"message\": \"Acesso nao autorizado. Autenticacao necessaria\"}") })),
                        @ApiResponse(responseCode = "409", description = "Conflict - Email already exists", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Email já cadastrado\"}"))),
        })
        @PostMapping("/register")
        public ResponseEntity<Void> createUser(@RequestBody @Valid UserDto userDto, BindingResult result) {
                String token = authService.registerUser(userDto, result);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .header("Authorization", "Bearer " + token)
                                .build();
        }

        @Operation(summary = "User login", description = "Authenticate user with email and password", responses = {
                        @ApiResponse(responseCode = "200", description = "User authentication successful."),
                        @ApiResponse(responseCode = "400", description = "Incorrect data submission", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(name = "Invalid email or password", value = "{\"errors\": [\"Email e senha são obrigatórios\", \"Email ou senha não conferem.\"]}"),
                                        @ExampleObject(name = "Invalid email", value = "{\"errors\": [\"Email no formato incorreto\"\"]}"),
                                        @ExampleObject(name = "Invalid email", value = "{\"errors\": [\"O e-mail é obrigatório\", \"Formato de e-mail inválido.\"]}"),
                        })),
                        @ApiResponse(responseCode = "403", description = "User inactivated", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário inativo\"}"))),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado\"}"))),
        })
        @PostMapping("/login")
        public ResponseEntity<Void> login(@RequestBody @Valid AuthenticationDto authenticationDto) {
                String token = authService.login(authenticationDto);
                return ResponseEntity.ok()
                                .header("Authorization", "Bearer " + token)
                                .build();
        }

        @Operation(summary = "Forgot password", description = "Send an email with a code to reset the password", responses = {
                        @ApiResponse(responseCode = "200", description = "Email sent successfully"),
                        @ApiResponse(responseCode = "400", description = "Incorrect data submission", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(name = "Invalid email", value = "{\"errors\": [\"O e-mail é obrigatório\", \"Formato de e-mail inválido.\"]}"),
                        }))
        })
        @PostMapping("/forgot-password")
        public ResponseEntity<Void> forgotPassword(@RequestBody @Valid UserEmailDto userEmailDto) {
                authService.forgotPassword(userEmailDto.email());
                return ResponseEntity.ok().build();
        }

        @Operation(summary = "Reset password", description = "Reset the user's password", responses = {
                        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
                        @ApiResponse(responseCode = "400", description = "Incorrect data submission", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(name = "Invalid code", value = "{\"errors\": [\"O código é obrigatório\", \"O código deve ter 6 caracteres.\"]}"),
                                        @ExampleObject(name = "Invalid password", value = "{\"errors\": [\"A senha é obrigatória\", \"A senha deve ter pelo menos 6 caracteres.\"]}"),
                                        @ExampleObject(name = "Passwords don't match", value = "{\"errors\": [\"As senhas não conferem\"]}"),
                        })),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado\"}"))),
                        @ApiResponse(responseCode = "409", description = "Conflict - Code expired", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Código expirado\"}"))),
        })
        @PostMapping("/reset-password")
        public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) {
                authService.resetPassword(
                        resetPasswordDto.email(),
                        resetPasswordDto.code(),
                        resetPasswordDto.password(),
                        resetPasswordDto.confirmPassword()
                );
                return ResponseEntity.ok().build();
        }

}
