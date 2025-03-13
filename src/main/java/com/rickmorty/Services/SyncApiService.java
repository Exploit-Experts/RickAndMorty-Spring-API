package com.rickmorty.Services;

import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.CharacterDto;
import com.rickmorty.Models.CharacterModel;
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
    private CharacterService characterService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private Config config;

    private final RestTemplate restTemplate;
    @Autowired
    private CharacterRepository characterRepository;

    public SyncApiService() {
        restTemplate = new RestTemplate();
    }

    @Scheduled(fixedDelay = 259200000)
    public void scheduledSync() {
        System.out.println("Sincronização iniciada");
        syncCharacters(null);
        System.out.println("Sincronização automática concluída!");
    }

    private void syncCharacters(String url) {
        System.out.println("Sincronização de personagens iniciada: " + url);
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
            try {
                CharacterModel character = this.characterService.saveCharacterByDto(dto);
                if (character != null) {
                    if (config.isAllowSendImages() && !character.isAvatarUploaded()) {
                        cloudinaryService.uploadFileFromUrl(
                                config.getApiBaseUrl() + "/character/avatar/" + character.getId() + ".jpeg",
                                character.getId(),
                                "characters"
                        );
                        character.setAvatarUploaded(true);
                        characterRepository.save(character);
                    }

                    System.out.println("Personagem: " + character.getName() + " salvo!");
                }
            } catch (Exception e) {
                System.err.println("Erro ao salvar personagem: " + dto.name() + " - " + e.getMessage());
            }
        }

        if (charactersList.info().next() != null) {
            syncCharacters(charactersList.info().next());
        }
    }
}
