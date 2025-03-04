package com.rickmorty.Controllers;

import com.rickmorty.DTO.AuthenticationDto;
import com.rickmorty.Services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/public/auth")
public class AuthenticationController {

    private final TokenService tokenService;

    public AuthenticationController(TokenService tokenService) {
        this.tokenService = tokenService;
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
    public ResponseEntity login(@RequestBody @Valid AuthenticationDto authenticationDto){
        String token = tokenService.login(authenticationDto);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .build();
    }
}
