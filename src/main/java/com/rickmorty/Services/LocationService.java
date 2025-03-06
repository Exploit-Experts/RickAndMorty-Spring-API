package com.rickmorty.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.InfoDto;
import com.rickmorty.DTO.LocationDto;
import com.rickmorty.Utils.Config;
import com.rickmorty.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;
import com.rickmorty.enums.SortLocation;
import com.rickmorty.interfaces.LocationServiceInterface;

@Slf4j
@Service
public class LocationService implements LocationServiceInterface {

    @Autowired
    Config config;

    private final ObjectMapper objectMapper;
    private final HttpClient client;

    public LocationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public ApiResponseDto<LocationDto> findAllLocations(Integer page, String name, String type, String dimension, SortLocation sort) {
        if (page != null && page < 1) throw new InvalidParameterException("Parâmetro page incorreto, deve ser um numero inteiro maior ou igual a 1");
        try {
            StringBuilder urlBuilder = new StringBuilder(config.getApiBaseUrl() + "/location?");
            if (page != null) urlBuilder.append("page=").append(page).append("&");
            if (name != null) urlBuilder.append("name=").append(name).append("&");
            if (type != null) urlBuilder.append("type=").append(type).append("&");
            if (dimension != null) urlBuilder.append("dimension=").append(dimension).append("&");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body() == null || response.body().isEmpty() || response.statusCode() == 404) throw new NotFoundException();

            ApiResponseDto<LocationDto> apiResponseDto = objectMapper.readValue(response.body(),
                    new TypeReference<ApiResponseDto<LocationDto>>() {
                    });
            return rewriteApiResponse(apiResponseDto, String.valueOf(sort));  
        } catch (NotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public LocationDto getLocationById(Long id) {
        if (id == null || id < 1) throw new InvalidIdException();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getApiBaseUrl() + "/location/" + id))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body().isEmpty() || response.statusCode() == 404) throw new LocationNotFoundException("Localização não encontrada");

            LocationDto location = objectMapper.readValue(response.body(), LocationDto.class);
            return rewriteLocationDto(location);
        } catch (LocationNotFoundException ex) {
            throw new LocationNotFoundException(ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ApiResponseDto<LocationDto> rewriteApiResponse(ApiResponseDto<LocationDto> apiResponseDto, String sort) {
        InfoDto updatedInfo = rewriteInfoDto(apiResponseDto.info());

        List<LocationDto> updatedResults = apiResponseDto.results().stream()
                .map(this::rewriteLocationDto)
                .sorted((l1, l2) -> compareLocations(l1, l2, sort))
                .collect(Collectors.toList());

        return new ApiResponseDto<>(updatedInfo, updatedResults);
    }

    private int compareLocations(LocationDto l1, LocationDto l2, String sort) {
        if (sort == null || sort.isEmpty()) {
            return 0;
        }
        switch (sort.toLowerCase()) {
            case "name_asc":
                return l1.name().compareToIgnoreCase(l2.name());
            case "name_desc":
                return l2.name().compareToIgnoreCase(l1.name());
            case "type_asc":
                return l1.type().compareToIgnoreCase(l2.type());
            case "type_desc":
                return l2.type().compareToIgnoreCase(l1.type());
            case "dimension_asc":
                return l1.dimension().compareToIgnoreCase(l2.dimension());
            case "dimension_desc":
                return l2.dimension().compareToIgnoreCase(l1.dimension());
            default:
                return 0;
        }
    }

    private InfoDto rewriteInfoDto(InfoDto originalInfo) {
        return new InfoDto(
                originalInfo.count(),
                originalInfo.pages(),
                originalInfo.next() != null ? originalInfo.next().replace(config.getApiBaseUrl() + "/location", config.getLocalBaseUrl() + "/locations") : null,
                originalInfo.prev() != null ? originalInfo.prev().replace(config.getApiBaseUrl() + "/location", config.getLocalBaseUrl() + "/locations") : null
        );
    }

    private LocationDto rewriteLocationDto(LocationDto location) {
        return new LocationDto(
                location.id(),
                location.name(),
                location.type(),
                location.dimension(),
                location.residents().stream()
                        .map(resident -> resident.replace(config.getApiBaseUrl() + "/character/",
                                config.getLocalBaseUrl() + "/characters/"))
                        .collect(Collectors.toList()),
                location.url().replace(config.getApiBaseUrl()+"/location/", config.getLocalBaseUrl() + "/locations/")
        );
    }

}