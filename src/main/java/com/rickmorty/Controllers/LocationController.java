package com.rickmorty.Controllers;

import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.LocationDto;
import com.rickmorty.Services.LocationService;
import com.rickmorty.enums.SortLocation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(summary = "Get all locations", description = "Get all locations from the Rick and Morty series", responses = {
            @ApiResponse(responseCode = "200", description = "Locations found"),
            @ApiResponse(responseCode = "404", description = "Locations not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Não encontrado\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid parameter", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid Page parameter", value = "{\"message\": \"Parâmetro page inválido\"}"),
                    @ExampleObject(name = "Invalid Page", value = "{\"message\": \"Parâmetro page incorreto, deve ser um numero inteiro maior ou igual a 1\"}"),
                    @ExampleObject(name = "Invalid Name", value = "{\"message\": \"Parâmetro name inválido\"}"),
                    @ExampleObject(name = "Invalid Type", value = "{\"message\": \"Parâmetro type inválido\"}"),
                    @ExampleObject(name = "Invalid Dimension", value = "{\"message\": \"Parâmetro dimension inválido\"}"),
                    @ExampleObject(name = "Invalid Sort", value = "{\"message\": \"Parâmetro sort inválido\"}")
            })),
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponseDto<LocationDto>> getAllLocations(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String dimension,
            @RequestParam(required = false) SortLocation sort) {
        ApiResponseDto<LocationDto> locations = locationService.findAllLocations(page, name, type, dimension, sort);
        return ResponseEntity.ok(locations);
    }

    @Operation(summary = "Get location by ID", description = "Get a specific location by its ID", responses = {
            @ApiResponse(responseCode = "200", description = "Location found"),
            @ApiResponse(responseCode = "404", description = "Location not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Localização não encontrada\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "InvalidParameter", value = "{\"message\": \"Parâmetro id inválido\"}"),
                    @ExampleObject(name = "InvalidID", value = "{\"message\": \"ID enviado inválido, o id deve ser um número inteiro maior ou igual a 1\"}")
            })),
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LocationDto getLocationById(@PathVariable Long id) {
        return locationService.getLocationById(id);
    }
}
