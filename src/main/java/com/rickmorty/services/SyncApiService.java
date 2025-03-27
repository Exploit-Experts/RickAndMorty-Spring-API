package com.rickmorty.services;

import com.rickmorty.dtos.ApiResponseDto;
import com.rickmorty.dtos.CharacterDto;
import com.rickmorty.dtos.EpisodeDto;
import com.rickmorty.models.CharacterModel;
import com.rickmorty.dtos.LocationDto;
import com.rickmorty.models.EpisodeModel;
import com.rickmorty.models.LocationModel;
import com.rickmorty.repositories.EpisodeRepository;
import com.rickmorty.repositories.LocationRepository;
import com.rickmorty.utils.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.rickmorty.repositories.CharacterRepository;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SyncApiService {

    @Autowired
    private CharacterService characterService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private Config config;

    @Autowired
    private LocationService locationService;

    private final RestTemplate restTemplate;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private EpisodeService episodeService;

    private static final Logger logger = LoggerFactory.getLogger(SyncApiService.class);

    public SyncApiService() {
        restTemplate = new RestTemplate();
    }

    @Scheduled(fixedRate = 1296000000)
    public void scheduledSync() {
        logger.info("Sincronização iniciada");
        syncLocations(null);
        syncCharacters(null);
        syncEpisode(null);
        logger.info("Sincronização automática concluída!");
    }

    private void syncLocations(String url) {
        logger.info("Sincronização de localizações iniciada: {}", url);
        if (url == null) {
            url = config.getApiBaseUrl() + "/location";
        }

        ResponseEntity<ApiResponseDto<LocationDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponseDto<LocationDto>>() {
                }
        );

        ApiResponseDto<LocationDto> locationsList = response.getBody();
        if (locationsList == null || locationsList.results() == null) {
            throw new RuntimeException("Falha ao obter dados da API.");
        }

        for (LocationDto dto : locationsList.results()) {
            try {
                LocationModel location = this.locationService.saveLocationByDto(dto);
                if (location != null) {
                    logger.info("Localização: {} salva!", location.getName());
                }
            } catch (Exception e) {
                System.err.println("Erro ao salvar localização: " + dto.name() + " - " + e.getMessage());
            }
        }

        if (locationsList.info().next() != null) {
            syncLocations(locationsList.info().next());
        }
    }

    private void syncCharacters(String url) {
        logger.info("Sincronização de personagens iniciada: {}", url);
        if (url == null) {
            url = config.getApiBaseUrl() + "/character";
        }

        ResponseEntity<ApiResponseDto<CharacterDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponseDto<CharacterDto>>() {
                }
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
                    }
                    Long locationId = extractIdFromUrl(dto.location().url());
                    locationRepository.findById(locationId).ifPresent(character::setLocationModel);;

                    characterRepository.save(character);
                    logger.info("Personagem: {} salvo!", character.getName());
                }
            } catch (Exception e) {
                System.err.println("Erro ao salvar personagem: " + dto.name() + " - " + e.getMessage());
            }
        }

        if (charactersList.info().next() != null) {
            syncCharacters(charactersList.info().next());
        }
    }

    protected void syncEpisode(String url) {
        if (url == null) {
            url = config.getApiBaseUrl() + "/episode";
        }

        logger.info("Sincronização de episódios iniciada: {}", url);

        ResponseEntity<ApiResponseDto<EpisodeDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponseDto<EpisodeDto>>() {}
        );

        ApiResponseDto<EpisodeDto> episodeList = response.getBody();
        if (episodeList == null || episodeList.results() == null) {
            throw new RuntimeException("Falha ao obter dados da API.");
        }

        for (EpisodeDto episode : episodeList.results()) {
            try {
                EpisodeModel episodeModel = this.episodeService.saveEpisodeByDto(episode);
                if (episodeModel != null) {

                    List<Long> characterIds = extractCharacterIdsFromUrls(episode.characters());

                    List<CharacterModel> characters = this.characterService.findByIds(characterIds);
                    for (CharacterModel character : characters) {
                        if (!character.getEpisodes().contains(episodeModel)) {
                            character.getEpisodes().add(episodeModel);
                        }

                        if (!episodeModel.getCharacters().contains(character)) {
                            episodeModel.getCharacters().add(character);
                        }
                    }

                    this.episodeRepository.save(episodeModel);
                    this.characterRepository.saveAll(characters);
                     logger.info("Episódio: " + episodeModel.getName() + " salvo!");

                }
            } catch (Exception e) {
                System.err.println("Erro ao salvar episódio: " + episode.name() + " - " + e.getMessage());
            }
        }

        if (episodeList.info().next() != null) {
            syncEpisode(episodeList.info().next());
        }
    }

    private List<Long> extractCharacterIdsFromUrls(List<String> characterUrls) {
        List<Long> characterIds = new ArrayList<>();
        for (String url : characterUrls) {
            Long characterId = extractIdFromUrl(url);
            characterIds.add(characterId);
        }
        return characterIds;
    }
    public long extractIdFromUrl(String url) {
        if (url == null || !url.matches(".*/\\d+$")) {
            throw new IllegalArgumentException("URL inválida: url=" + url);
        }

        return Long.parseLong(url.substring(url.lastIndexOf('/') + 1));
    }
}
