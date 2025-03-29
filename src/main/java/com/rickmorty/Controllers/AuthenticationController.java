package com.rickmorty.Controllers;

import com.rickmorty.DTO.AuthenticationDto;
import com.rickmorty.DTO.UserDto;
import com.rickmorty.Services.AuthService;
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
@RequestMapping("api/v1/auth")
public class AuthenticationController {

    private final AuthService authService;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Create a new user",
            description = "Create a new user with the provided details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created"),
                    @ApiResponse(responseCode = "400", description = "Incorrect data submission",
                            content = @Content(mediaType = "application/json",
                                    examples = { @ExampleObject(name = "Invalid name", value = "{\"errors\": [\"O nome é obrigatório\", \"O nome deve ter entre 3 e 50 caracteres.\"]}"),
                                            @ExampleObject(name = "Invalid surname", value = "{\"errors\": [\"O sobrenome é obrigatório\", \"O sobrenome deve ter entre 3 e 50 caracteres.\"]}"),
                                            @ExampleObject(name = "Invalid email", value = "{\"errors\": [\"O e-mail é obrigatório\", \"Formato de e-mail inválido.\"]}"),
                                            @ExampleObject(name = "Invalid password", value = "{\"errors\": [\"A senha é obrigatória\", \"A senha deve ter pelo menos 6 caracteres.\"]}")
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "User not authenticate",
                            content = @Content(mediaType = "application/json",
                                    examples = {@ExampleObject(name = "Authentication requered", value = "{\"message\": \"Acesso nao autorizado. Autenticacao necessaria\"}")})),
                    @ApiResponse(responseCode = "409", description = "Conflict - Email already exists",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"Email já cadastrado\"}"))),
            })
    @PostMapping("/register")
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserDto userDto, BindingResult result) {
        String token = authService.registerUser(userDto, result);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Authorization", "Bearer " + token)
                .build();
    }

    @Operation(summary = "User login",
            description = "Authenticate user with email and password",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User authentication successful."),
                    @ApiResponse(responseCode = "400", description = "Incorrect data submission",
                            content = @Content(mediaType = "application/json",
                                    examples = { @ExampleObject(name = "Invalid email or password", value = "{\"errors\": [\"Email e senha são obrigatórios\", \"Email ou senha não conferem.\"]}"),
                                            @ExampleObject(name = "Invalid email", value = "{\"errors\": [\"Email no formato incorreto\"\"]}"),
                                            @ExampleObject(name = "Invalid email", value = "{\"errors\": [\"O e-mail é obrigatório\", \"Formato de e-mail inválido.\"]}"),
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "User inactivated", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário inativo\"}"))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado\"}"))),
            })
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid AuthenticationDto authenticationDto){
        String token = authService.login(authenticationDto);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .build();
    }
}
