package com.rickmorty.Controllers;

import com.rickmorty.DTO.FavoriteResponseDto;
import com.rickmorty.Services.FavoriteService;
import com.rickmorty.enums.SortFavorite;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rickmorty.DTO.FavoriteDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequestMapping("/api/v1/favorites")
@SecurityRequirement(name = "BearerAuth")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Operation(summary = "Create a new favorite",
            description = "Create a new favorite for a user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Favorite created"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Malformed JSON", description = "When the api user send a invalid json", value = "{\"message\": \"Erro no corpo da requisição: o JSON está mal formatado ou contém valores inválidos\"}"),
                                            @ExampleObject(name = "Invalid apiId", description = "When the api user send a apiId inválid. Ex: apiId: 44a.", value = "{\"message\": \"Parâmetro apiId inválido. Deve ser um número positivo maior que zero\"}"),
                                            @ExampleObject(name = "Invalid userId", description = "When the api user send a userId inválid", value = "{\"message\": \"Parâmetro userId inválido. Deve ser um número positivo maior que zero\"}")
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Item not found",
                            content = @Content(mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "itemType not found", description = "When an apiId is sent that does not exist. Ex: apiId: 1000000, itemType: \"LOCATION\"", value = "{\"message\": \"LOCATION não encontrada para o ID\"}"),
                                            @ExampleObject(name = "User not found", value = "{\"message\": \"Usuário não encontrado\"}")
                                    })),
                    @ApiResponse(responseCode = "401", description = "User not authenticate",
                            content = @Content(mediaType = "application/json",
                                    examples = {@ExampleObject(name = "Authentication requered", value = "{\"message\": \"Acesso nao autorizado. Autenticacao necessaria\"}")})),
                    @ApiResponse(responseCode = "409", description = "Conflict - Favorite already exists",
                                 content = @Content(mediaType = "application/json",
                                                    examples = @ExampleObject(value = "{\"message\": \"O favorito já está cadastrado\"}"))),
            })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> createFavorite(@RequestBody @Valid FavoriteDto favoriteDto, BindingResult result) {
        favoriteService.create(favoriteDto, result);
        return new ResponseEntity<>(HttpStatus.CREATED);
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
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<FavoriteResponseDto>> getAllFavorites(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "ASC") SortFavorite sort) {

        Page<FavoriteResponseDto> favorites = favoriteService.getAllFavorites(userId, page, sort);

        return ResponseEntity.ok(favorites);
    }

    @Operation(summary = "Remove a specific favorite for a user",
            description = "Remove a specific favorite for a user by user ID and favorite ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Favorite removed"),
                    @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                            content = @Content(mediaType = "application/json",
                                    examples = {
                                        @ExampleObject(name = "Inválid parameter", description = "When send a inválid parameter. Ex: user: 1aaa", value = "{\"message\": \"Parâmetro userId inválido.\"}"),
                                        @ExampleObject(name = "Inválid direction", value = "{\"message\": \"Direção de sort inválida\"}"),
                                        @ExampleObject(name = "Inválid sort field", value = "{\"message\": \"Campo sort inválido\"}")
                                    })),
                    @ApiResponse(responseCode = "401", description = "User not authenticate",
                            content = @Content(mediaType = "application/json",
                                    examples = {@ExampleObject(name = "Authentication requered", value = "{\"message\": \"Acesso nao autorizado. Autenticacao necessaria\"}")})),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND",
                                 content = @Content(mediaType = "application/json", 
                                                    examples = {
                                                            @ExampleObject(name = "User not found", value = "{\"message\": \"Usuário não encontrado\"}"),
                                                            @ExampleObject(name = "Favorite not found", value = "{\"message\": \"Favorito não cadastrado\"}")
                                                    })),
        })
    @DeleteMapping("/{userId}/{favoriteId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> removeFavorite(@PathVariable Long userId, @PathVariable Long favoriteId) {
        favoriteService.removeFavorite(userId, favoriteId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> removeFavoritesByUserId(@PathVariable Long userId) {
        favoriteService.removeAllFavoritesByUserId(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}