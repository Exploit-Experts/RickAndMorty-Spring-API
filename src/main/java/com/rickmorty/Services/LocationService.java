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


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class LocationService {

    @Autowired
    Config config;

    private final ObjectMapper objectMapper;
    private final HttpClient client;

    @Autowired
    public LocationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.client = HttpClient.newHttpClient();
    }

    public ApiResponseDto findAllLocations(Integer page) {
        try {
            if (page != null && page < 1) {
                throw new InvalidParameterException("Page precisa ser um número positivo.");
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getApiBaseUrl() + "/location/?page=" + page))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body() == null || response.body().isEmpty() || response.statusCode() == 404) throw new PageNotFoundException();

            ApiResponseDto<LocationDto> apiResponseDto = objectMapper.readValue(response.body(),
                    new TypeReference<ApiResponseDto<LocationDto>>() {
                    });
            return rewriteApiResponse(apiResponseDto);
        } catch (InvalidParameterException e) {
            throw new InvalidParameterException(e.getMessage());
        } catch (PageNotFoundException e) {
            throw new PageNotFoundException();
        }catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }


    public LocationDto getLocationById(String id) throws IOException, InterruptedException {
        try {
            if (id == null || id.isEmpty()) {
                throw new InvalidIdException("Você precisa enviar um id");
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getApiBaseUrl() + "/location/" + id))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body().isEmpty() || response.statusCode() == 404) throw new LocationNotFoundException("Localizações não encontradas");

            LocationDto location = objectMapper.readValue(response.body(), LocationDto.class);
            return rewriteLocationDto(location);
        } catch (InvalidIdException ex) {
            throw new InvalidIdException(ex.getMessage());
        } catch (LocationNotFoundException ex) {
            throw new LocationNotFoundException(ex.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }

    private ApiResponseDto<LocationDto> rewriteApiResponse(ApiResponseDto<LocationDto> apiResponseDto) {
        InfoDto updatedInfo = rewriteInfoDto(apiResponseDto.info());

        List<LocationDto> updatedResults = new ArrayList<>();
        for (LocationDto location : apiResponseDto.results()) {
            LocationDto updatedLocation = rewriteLocationDto(location);
            updatedResults.add(updatedLocation);
        }
        ApiResponseDto<LocationDto> responseRewrited = new ApiResponseDto<LocationDto>(updatedInfo, updatedResults);
        if (responseRewrited.results().isEmpty()) {
            throw new RewriteErrorException("Erro ao reescrever resposta");
        }

        return new ApiResponseDto<>(updatedInfo, updatedResults);
    }


    private InfoDto rewriteInfoDto(InfoDto originalInfo) {
        return new InfoDto(
                originalInfo.count(),
                originalInfo.pages(),
                originalInfo.next() != null ? originalInfo.next().replace(config.getApiBaseUrl() + "/location/", config.getLocalBaseUrl() + "/locations") : null,
                originalInfo.prev() != null ? originalInfo.prev().replace(config.getApiBaseUrl() + "/location/", config.getLocalBaseUrl() + "/locations") : null
        );
    }

    private LocationDto rewriteLocationDto(LocationDto location) {
        LocationDto locationRewrited = new LocationDto(
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
        if (locationRewrited == null) {
            throw new RewriteErrorException("Erro ao reescrever localização");
        }
        return locationRewrited;
    }


}