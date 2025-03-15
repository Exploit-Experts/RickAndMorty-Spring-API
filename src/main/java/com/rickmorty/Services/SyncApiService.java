package com.rickmorty.Services;

import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.LocationDto;
import com.rickmorty.Models.LocationModel;
import com.rickmorty.Repository.CharacterRepository;
import com.rickmorty.Repository.EpisodeRepository;
import com.rickmorty.Repository.LocationRepository;
import com.rickmorty.Utils.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SyncApiService {

    @Autowired
    private Config config;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    private final RestTemplate restTemplate;
    @Autowired
    private LocationService locationService;

    public SyncApiService() {
        restTemplate = new RestTemplate();
    }

    @Scheduled(fixedDelay = 259200000)
    public void scheduledSync() {
        System.out.println("Sincronização iniciada");
        syncLocations(null);
        System.out.println("Sincronização automática concluída!");
    }

    private void syncLocations(String url) {
        System.out.println("Sincronização de localizações iniciada: " + url);
        if (url == null) {
            url = config.getApiBaseUrl() + "/location";
        }

        ResponseEntity<ApiResponseDto<LocationDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponseDto<LocationDto>>() {}
        );

        ApiResponseDto<LocationDto> locationsList = response.getBody();
        if (locationsList == null || locationsList.results() == null) {
            throw new RuntimeException("Falha ao obter dados da API.");
        }

        for (LocationDto dto : locationsList.results()) {
            try {
                LocationModel location = this.locationService.saveLocationByDto(dto);
                if (location != null) {
                    System.out.println("Localização: " + location.getName() + " salva!");
                }
            } catch (Exception e) {
                System.err.println("Erro ao salvar localização: " + dto.name() + " - " + e.getMessage());
            }
        }

        if (locationsList.info().next() != null) {
            syncLocations(locationsList.info().next());
        }
    }

}
