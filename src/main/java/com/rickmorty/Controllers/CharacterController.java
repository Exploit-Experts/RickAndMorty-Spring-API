package com.rickmorty.Controllers;

import com.rickmorty.Services.CharacterService;
import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.CharacterDto;
import com.rickmorty.enums.Gender;
import com.rickmorty.enums.LifeStatus;
import com.rickmorty.enums.SortOrder;
import com.rickmorty.enums.Species;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.util.Map;

@RestController
@RequestMapping("api/v1/characters")
public class CharacterController {

        @Autowired
        private CharacterService characterService;

        @Operation(summary = "Get all characters", description = "Get all characters from the Rick and Morty series", responses = {
                        @ApiResponse(responseCode = "200", description = "Characters found"),
                        @ApiResponse(responseCode = "404", description = "Characters not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Não encontrado\"}"))),
                        @ApiResponse(responseCode = "400", 
                        description = "Invalid parameter", 
                        content = @Content(mediaType = "application/json", examples = {
                                @ExampleObject(name= "Invalid Page parameter",value = "{\"message\": \"Parâmetro page inválido\"}"),
                                @ExampleObject(name= "Invalid Page",value = "{\"message\": \"Parmetro page incorreto, deve ser um numero inteiro maior ou igual a 1\"}"),
                                @ExampleObject(name= "Invalid Status",value = "{\"message\": \"Parâmetro status inválido\"}"),
                                @ExampleObject(name= "Invalid Species",value = "{\"message\": \"Parâmetro species inválido\"}"),
                                @ExampleObject(name= "Invalid Sort", value = "{\"message\": \"Parâmetro sort inválido\"}"),
                                @ExampleObject(name= "Inavlid Gender", value = "{\"message\": \"Parâmetro gender inválido\"}")
                        })),
        })
        @GetMapping
        public ResponseEntity<Map<String, Object>> getAllCharacters(
                @RequestParam(required = false) Integer page,
                @RequestParam(required = false) String name,
                @RequestParam(required = false) LifeStatus status,
                @RequestParam(required = false) Species species,
                @RequestParam(required = false) String type,
                @RequestParam(required = false) Gender gender,
                @RequestParam(required = false) SortOrder sort) {

                Map<String, Object> characters = characterService.findAllCharacters(page, name, status, species, type, gender, sort);
                return ResponseEntity.ok(characters);
        }

        @Operation(summary = "Get character by ID", description = "Get a specific character by its ID", responses = {
                        @ApiResponse(responseCode = "200", description = "Character found"),
                        @ApiResponse(responseCode = "404", description = "Character not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Character não encontrado para o ID\"}"))),
                        @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = @Content(mediaType = "application/json", examples = {
                                @ExampleObject(name="InvalidParameter", value = "{\"message\": \"Parâmetro id inválido\"}"),
                                @ExampleObject(name="InvalidID", value = "{\"message\": \"ID enviado inválido, o id deve ser um número inteiro maior ou igual a 1\"}")
                        })),
        })
        @GetMapping("/{id}")
        public ResponseEntity<CharacterDto> getCharacterById(@PathVariable Long id) {
                CharacterDto character = characterService.findACharacterById(id);
                return new ResponseEntity<>(character, HttpStatus.OK);
        }

        @Operation(summary = "Get character avatar", description = "Get the avatar of a specific character by its ID", responses = {
                        @ApiResponse(responseCode = "200", description = "Avatar found", content = @Content(mediaType = "image/jpeg")),
                        @ApiResponse(responseCode = "404", description = "Avatar not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Character não encontrado para o ID\"}"))),
                        @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = @Content(mediaType = "application/json", examples = {
                                @ExampleObject(name="InvalidParameter", value = "{\"message\": \"Parâmetro id inválido\"}"),
                                @ExampleObject(name="InvalidID", value = "{\"message\": \"ID enviado inválido, o id deve ser um número inteiro maior ou igual a 1\"}")
                        }))
        })
        @GetMapping("/avatar/{id}.jpeg")
        public ResponseEntity<byte[]> getCharacterAvatar(@PathVariable Long id) {
                return characterService.findCharacterAvatar(id);
        }
}
