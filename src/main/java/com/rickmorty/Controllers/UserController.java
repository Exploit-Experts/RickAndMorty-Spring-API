package com.rickmorty.Controllers;

import com.rickmorty.DTO.UserDto;
import com.rickmorty.DTO.UserPatchDto;
import com.rickmorty.Services.FavoriteService;
import com.rickmorty.Services.UserService;
import com.rickmorty.enums.FavoriteTypes;
import com.rickmorty.enums.SortFavorite;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    private final UserService userService;
    private final FavoriteService favoriteService;

    public UserController(UserService userService, FavoriteService favoriteService) {
        this.userService = userService;
        this.favoriteService = favoriteService;
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
                    @ApiResponse(responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"Você não tem permissão para acessar esse recurso\"}"))),
                    @ApiResponse(responseCode = "409", description = "Conflict - Email already exists", 
                                 content = @Content(mediaType = "application/json", 
                                                    examples = @ExampleObject(value = "{\"message\": \"Email já cadastrado\"}"))),
            })
    @PostMapping()
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserDto userDto, BindingResult result) {
        userService.saveUser(userDto, result);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing user",
            description = "Update an existing user with the provided details",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User updated"),
                    @ApiResponse(responseCode = "400", description = "Incorrect data submission",

                                    content = @Content(mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Zero ID sent as a parameter", value = "{\"message\": \"ID enviado inválido, o id deve ser um numero inteiro maior ou igual a 1\"}"),
                                            @ExampleObject(name = "Invalid parameter", value = "{\"message\": \"Parâmetro id inválido\"}"),
                                            @ExampleObject(name = "Invalid name", value = "{\"errors\": [\"O nome é obrigatório\", \"O nome deve ter entre 3 e 50 caracteres.\"]}"),
                                            @ExampleObject(name = "Invalid surname", value = "{\"errors\": [\"O sobrenome é obrigatório\", \"O sobrenome deve ter entre 3 e 50 caracteres.\"]}"),
                                            @ExampleObject(name = "Invalid email", value = "{\"errors\": [\"O e-mail é obrigatório\", \"Formato de e-mail inválido.\"]}"),
                                            @ExampleObject(name = "Invalid password", value = "{\"errors\": [\"A senha é obrigatória\", \"A senha deve ter pelo menos 6 caracteres.\"]}")
                                        }
                                    )
                    ),
                    @ApiResponse(responseCode = "401", description = "User not authenticate",
                            content = @Content(mediaType = "application/json",
                                    examples = {@ExampleObject(name = "Authentication requered", value = "{\"message\": \"Acesso nao autorizado. Autenticacao necessaria\"}")})),
                    @ApiResponse(responseCode = "404", description = "User not found", 
                                 content = @Content(mediaType = "application/json", 
                                                    examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado\"}"))),
                    @ApiResponse(responseCode = "409", description = "Conflict - Email already exists", 
                                 content = @Content(mediaType = "application/json", 
                                                    examples = @ExampleObject(value = "{\"message\": \"Email já cadastrado\"}"))),
            })
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid UserDto userDto, BindingResult result) {
        userService.updateUser(id, userDto, result);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Patch an existing user",
            description = "Patch an existing user with the provided details",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User patched"),
                    @ApiResponse(responseCode = "400", description = "Incorrect data submission",
                            content = @Content(mediaType = "application/json",
                                    examples = {@ExampleObject(name = "Zero ID sent as a parameter", value = "{\"message\": \"ID enviado inválido, o id deve ser um número inteiro maior ou igual a 1\"}"),
                                            @ExampleObject(name = "Invalid parameter", value = "{\"message\": \"Parâmetro id inválido\"}"),
                                            @ExampleObject(name = "Invalid name", value = "{\"errors\": [\"Nome não pode estar vazio\", \"O nome deve ter entre 3 e 50 caracteres.\"]}"),
                                            @ExampleObject(name = "Invalid surname", value = "{\"errors\": [\"Sobrenome não pode estar vazio\", \"O sobrenome deve ter entre 3 e 50 caracteres.\"]}"),
                                            @ExampleObject(name = "Invalid email", value = "{\"errors\": [\"Email não pode estar vazio\", \"Formato de e-mail inválido.\"]}"),
                                            @ExampleObject(name = "Invalid password", value = "{\"errors\": [\"Senha não pode estar vazia \", \"A senha deve ter pelo menos 6 caracteres.\"]}")
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "User not authenticate",
                            content = @Content(mediaType = "application/json",
                                    examples = {@ExampleObject(name = "Authentication requered", value = "{\"message\": \"Acesso nao autorizado. Autenticacao necessaria\"}")})),
                    @ApiResponse(responseCode = "404", description = "User not found", 
                                 content = @Content(mediaType = "application/json", 
                                                    examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado\"}"))),
                    @ApiResponse(responseCode = "409", description = "Conflict - Email already exists", 
                                 content = @Content(mediaType = "application/json", 
                                                    examples = @ExampleObject(value = "{\"message\": \"Email já cadastrado\"}"))),
            })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patch(@PathVariable Long id, @RequestBody @Valid UserPatchDto userPatchDto, BindingResult result) {
        userService.patchUser(id, userPatchDto, result);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete a user",
            description = "Delete a user by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameter",
                            content = @Content(mediaType = "application/json",
                                    examples = {@ExampleObject(name = "Zero ID sent as a parameter", value = "{\"message\": \"ID enviado inválido, o id deve ser um número válido e positivo\"}"),
                                                @ExampleObject(name = "Invalid parameter", value = "{\"message\": \"Parâmetro id inválido\"}")})),
                    @ApiResponse(responseCode = "401", description = "User not authenticate",
                            content = @Content(mediaType = "application/json",
                                    examples = {@ExampleObject(name = "Authentication requered", value = "{\"message\": \"Acesso nao autorizado. Autenticacao necessaria\"}")})),
                    @ApiResponse(responseCode = "404", description = "User not found", 
                                 content = @Content(mediaType = "application/json", 
                                                    examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado\"}"))),
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get all favorites for a user",
        description = "Retrieve all favorites for a specific user",
        responses = {
            @ApiResponse(responseCode = "200", description = "Favorites found"),
            @ApiResponse(responseCode = "400", description = "Invalid parameter. Ex: send a letter in userId",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Parâmetro userId inválido.\"}"))),
            @ApiResponse(responseCode = "401", description = "User not authenticate",
                content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "Authentication requered", value = "{\"message\": \"Acesso nao autorizado. Autenticacao necessaria\"}")})),
            @ApiResponse(
                responseCode = "404",
                description = "NOT FOUND",
                content = @Content(mediaType = "application/json",
                    examples = {
                        @ExampleObject(name = "User not found", value = "{\"message\": \"Usuário não encontrado\"}"),
                        @ExampleObject(name = "User hasn't favorites", value = "{\"message\": \"O usuário não tem favoritos cadastrados\"}")
                    })),
        })
    @GetMapping("/favorites/{userId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<?>> getAllUserFavorites(
        @RequestHeader("Authorization") String token,
        @PathVariable Long userId,
        @RequestParam FavoriteTypes favoriteType,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam SortFavorite sort,
        @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        Page<?> favorites = favoriteService.getAllFavoritesByUserId(token, userId, favoriteType, page, sort, direction);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("favorites/{userId}/{favoriteId}")
    public ResponseEntity<?> getFavoriteById(
        @PathVariable Long userId,
        @PathVariable Long favoriteId
    ) {
        Object favorite = favoriteService.getFavoriteById(userId, favoriteId);
        return ResponseEntity.ok(favorite);
    }

    @Operation(summary = "Remove all favorites for a user",
        description = "Remove all favorites for a user by user ID",
        responses = {
            @ApiResponse(responseCode = "204", description = "All favorites removed"),
            @ApiResponse(responseCode = "400", description = "Invalid parameter. Ex: send a letter in userId",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Parâmetro userId inválido.\"}"))),
            @ApiResponse(responseCode = "401", description = "User not authenticate",
                content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "Authentication requered", value = "{\"message\": \"Acesso nao autorizado. Autenticacao necessaria\"}")})),
            @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(mediaType = "application/json",
                    examples = {
                        @ExampleObject(name = "User not found", value = "{\"message\": \"Usuário não encontrado\"}"),
                        @ExampleObject(name = "User hasn't favorites", value = "{\"message\": \"O usuário não tem favoritos cadastrados\"}")
                    })),
        })
    @DeleteMapping("/favorites/{userId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> removeFavoritesByUserId(
        @RequestHeader("Authorization") String token,
        @PathVariable Long userId
    ) {
        favoriteService.removeAllFavoritesByUserId(token, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}