package com.rickmorty.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.CharacterDto;
import com.rickmorty.DTO.InfoDto;
import com.rickmorty.DTO.LocationCharacterDto;
import com.rickmorty.Utils.Config;
import com.rickmorty.enums.Gender;
import com.rickmorty.enums.LifeStatus;
import com.rickmorty.enums.SortOrder;
import com.rickmorty.enums.Species;
import com.rickmorty.exceptions.CharacterNotFoundException;
import com.rickmorty.exceptions.InvalidIdException;
import com.rickmorty.exceptions.InvalidParameterException;
import com.rickmorty.exceptions.NotFoundException;
import com.rickmorty.interfaces.CharacterServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CharacterService implements CharacterServiceInterface {

    @Autowired
    Config config;

    @Override
    public ApiResponseDto<CharacterDto> findAllCharacters(Integer page, String name, LifeStatus status, Species species, String type, Gender gender, SortOrder sort) {
        if (page != null && page < 0) throw new InvalidParameterException("Parâmetro page incorreto, deve ser um numero inteiro maior ou igual a 1");

        try {
            HttpClient client = HttpClient.newHttpClient();
            StringBuilder urlBuilder = new StringBuilder(config.getApiBaseUrl() + "/character?");

            if (page != null) urlBuilder.append("page=").append(page).append("&");
            if (species != null) urlBuilder.append("species=").append(species).append("&");
            if (gender != null) urlBuilder.append("gender=").append(gender).append("&");
            if (status != null) urlBuilder.append("status=").append(status).append("&");
            if (type != null){
                type = type.replace(" ", "+");
                urlBuilder.append("type=").append(type).append("&");
            }
            if (name != null){
                name = name.replace(" ", "+");
                urlBuilder.append("name=").append(name).append("&");
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) throw new NotFoundException();
            ObjectMapper objectMapper = new ObjectMapper();
            ApiResponseDto<CharacterDto> apiResponseDto = objectMapper.readValue(response.body(), new TypeReference<ApiResponseDto<CharacterDto>>() {});

            return rewriteApiResponse(apiResponseDto, String.valueOf(sort));

        } catch (InvalidParameterException e){
            throw new InvalidParameterException(e.getMessage());
        }catch (NotFoundException e) {
            throw new NotFoundException();
        }catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public CharacterDto findACharacterById(Long id) {
        if (id == null || id < 1) throw new InvalidIdException();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getApiBaseUrl() + "/character/" + id))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();

            if (response.statusCode() == 404) throw new CharacterNotFoundException();

            CharacterDto character = objectMapper.readValue(response.body(), CharacterDto.class);
            return rewriteCharacterDto(character);
        } catch (CharacterNotFoundException e) {
            throw new CharacterNotFoundException();
        } catch (Exception e) {
            log.error("Erro ao buscar personagem por ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public ResponseEntity<byte[]> findCharacterAvatar(Long id) {
        if (id == null || id < 1) throw new InvalidIdException();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getApiBaseUrl() + "/character/" + id))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) throw new CharacterNotFoundException();
            ObjectMapper objectMapper = new ObjectMapper();
            String imageUrl = objectMapper.readTree(response.body()).get("image").asText();
            byte[] imageBytes = downloadImage(URI.create(imageUrl).toURL());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "image/jpeg");

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        }catch (CharacterNotFoundException e) {
            throw new CharacterNotFoundException();
        }catch (Exception e) {
            log.error("Erro ao buscar avatar do personagem: " + e.getMessage(), e);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private ApiResponseDto<CharacterDto> rewriteApiResponse(ApiResponseDto<CharacterDto> apiResponseDto, String sort) {
        InfoDto updatedInfo = rewriteInfoDto(apiResponseDto.info());

        List<CharacterDto> updatedResults = apiResponseDto.results().stream()
                .map(this::rewriteCharacterDto)
                .sorted((c1, c2) -> compareCharacters(c1, c2, sort))
                .collect(Collectors.toList());

        return new ApiResponseDto<>(updatedInfo, updatedResults);
    }

    private int compareCharacters(CharacterDto c1, CharacterDto c2, String sort) {
        if (sort == null || sort.isEmpty()) {
            return 0;
        }
        switch (sort.toLowerCase()) {
            case "name_asc":
                return c1.name().compareToIgnoreCase(c2.name());
            case "name_desc":
                return c2.name().compareToIgnoreCase(c1.name());
            case "status_asc":
                return c1.status().compareToIgnoreCase(c2.status());
            case "status_desc":
                return c2.status().compareToIgnoreCase(c1.status());
            default:
                return 0;
        }
    }

    private InfoDto rewriteInfoDto(InfoDto originalInfo) {
        String nextUrl = Optional.ofNullable(originalInfo.next())
                .map(next -> next.replace(config.getApiBaseUrl() + "/character",
                        config.getLocalBaseUrl() + "/characters"))
                .orElse(null);
    
        String prevUrl = Optional.ofNullable(originalInfo.prev())
                .map(prev -> prev.replace(config.getApiBaseUrl() + "/character",
                        config.getLocalBaseUrl() + "/characters"))
                .orElse(null);
    
        return new InfoDto(
                originalInfo.count(),
                originalInfo.pages(),
                nextUrl,
                prevUrl);
    }

    private CharacterDto rewriteCharacterDto(CharacterDto character) {
        LocationCharacterDto characterLocation = character.location() != null
                ? new LocationCharacterDto(
                character.location().name(),
                character.location().url().replace(config.getApiBaseUrl() + "/location/", config.getLocalBaseUrl() + "/locations/"))
                : null;

        return new CharacterDto(
                character.id(),
                character.name(),
                character.status(),
                character.species(),
                character.type(),
                character.gender(),
                character.image().replace(config.getApiBaseUrl() + "/character/", config.getLocalBaseUrl() + "/characters/"),
                character.episode().stream()
                        .map(episode -> episode.replace(config.getApiBaseUrl() + "/episode/", config.getLocalBaseUrl() + "/episodes/"))
                        .collect(Collectors.toList()),
                characterLocation
        );
    }



    private byte[] downloadImage(URL imageUrl) throws Exception {
        try (InputStream in = imageUrl.openStream()) {
            return in.readAllBytes();
        }
    }
}