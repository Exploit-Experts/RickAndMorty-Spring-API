package com.rickmorty.Services;

import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.CharacterDto;
import com.rickmorty.DTO.LocationDto;
import com.rickmorty.Models.CharacterModel;
import com.rickmorty.Models.LocationModel;
import com.rickmorty.Repository.CharacterRepository;
import com.rickmorty.Repository.EpisodeRepository;
import com.rickmorty.Repository.LocationRepository;
import com.rickmorty.Utils.Config;
import com.rickmorty.enums.Gender;
import com.rickmorty.enums.LifeStatus;
import jakarta.transaction.Transactional;
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
    private CharacterService characterService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    Config config;

    private final RestTemplate restTemplate;

    public SyncApiService() {
        restTemplate = new RestTemplate();
    }

    @Scheduled(fixedRate = 86400000)
    @Transactional
    public void scheduledSync() {
        System.out.println("Sincronização iniciada");
        syncCharacters(null);
        System.out.println("Sincronização automática concluída!");
    }

    @Transactional
    protected void syncCharacters(String url) {
        if (url == null) {
            url = config.getApiBaseUrl() + "/character";
        }

        ResponseEntity<ApiResponseDto<CharacterDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponseDto<CharacterDto>>() {}
        );

        ApiResponseDto<CharacterDto> charactersList = response.getBody();
        if (charactersList == null || charactersList.results() == null) {
            throw new RuntimeException("Falha ao obter dados da API.");
        }

        for (CharacterDto dto : charactersList.results()) {
            CharacterModel character = this.characterService.saveCharacterByDto(dto);
            if (character != null) {
                // implementar logica para setar a localização
            }
        }

        if (charactersList.info().next() != null) {
            syncCharacters(charactersList.info().next());
        }
    }

    private Long extractIdFromUrl(String url) {
        String[] parts = url.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }
}
