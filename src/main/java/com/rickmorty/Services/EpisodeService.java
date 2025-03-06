package com.rickmorty.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.EpisodeDto;
import com.rickmorty.DTO.InfoDto;
import com.rickmorty.Utils.Config;
import com.rickmorty.enums.SortEpisode;
import com.rickmorty.exceptions.EpisodeNotFoundException;
import com.rickmorty.exceptions.InvalidIdException;
import com.rickmorty.exceptions.InvalidParameterException;
import com.rickmorty.exceptions.NotFoundException;
import com.rickmorty.interfaces.EpisodeServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EpisodeService implements EpisodeServiceInterface {

    @Autowired
    Config config;

    @Override
    public ApiResponseDto<EpisodeDto> findAllEpisodes(Integer page, String name, String episode, SortEpisode sort) {

        if (episode != null && !Pattern.matches("^S\\d{2}(E\\d{2})?$", episode.toUpperCase())) throw new InvalidParameterException("Parâmetro episode não está no formato correto. Esperado: SXXEXX");

        try {

            if (page != null && page <= 0) throw new InvalidParameterException("Parâmetro page incorreto, deve ser um numero inteiro maior ou igual a 1");

            StringBuilder urlBuilder = new StringBuilder(config.getApiBaseUrl() + "/episode?");
            if (page != null) urlBuilder.append("page=").append(page).append("&");
            if (name != null){
                name = name.replace(" ", "+");
                urlBuilder.append("name=").append(name).append("&");
            }
            if (episode != null) urlBuilder.append("episode=").append(episode).append("&");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) throw new NotFoundException();

            ObjectMapper objectMapper = new ObjectMapper();

            ApiResponseDto<EpisodeDto> apiResponseDto = objectMapper.readValue(response.body(),
                    new TypeReference<ApiResponseDto<EpisodeDto>>() {
                    });
            return rewriteApiResponse(apiResponseDto, String.valueOf(sort));

        }catch (InvalidParameterException e){
            throw new InvalidParameterException(e.getMessage());
        } catch (NotFoundException e){
            throw new NotFoundException();
        }catch (Exception e) {
            log.error("Erro ao buscar episódios: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public EpisodeDto findEpisodeById(Long id) {
        try {
            if (id == null || id < 1) throw new InvalidIdException();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getApiBaseUrl() + "/episode/" + id))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) throw new EpisodeNotFoundException();
            ObjectMapper objectMapper = new ObjectMapper();

            EpisodeDto episode = objectMapper.readValue(response.body(), EpisodeDto.class);
            return rewriteEpisodeDto(episode);

        } catch (InvalidIdException e) {
            throw new InvalidIdException();
        } catch (NumberFormatException e) {
            throw new InvalidIdException();
        } catch (EpisodeNotFoundException e) {
            throw new EpisodeNotFoundException();
        } catch (Exception e) {
            log.error("Erro ao buscar episódio por ID: " + e.getMessage(), e);
        }
        return null;
    }

    private ApiResponseDto<EpisodeDto> rewriteApiResponse(ApiResponseDto<EpisodeDto> apiResponseDto, String sort) {
        InfoDto updatedInfo = rewriteInfoDto(apiResponseDto.info());

        List<EpisodeDto> updatedResults = apiResponseDto.results().stream()
                .map(this::rewriteEpisodeDto)
                .sorted((e1, e2) -> compareEpisodes(e1, e2, sort))
                .collect(Collectors.toList());

        return new ApiResponseDto<>(updatedInfo, updatedResults);
    }

    private int compareEpisodes(EpisodeDto e1, EpisodeDto e2, String sort) {
        if (sort == null || sort.isEmpty()) {
            return 0;
        }

        switch (sort.toLowerCase()) {
            case "name":
                return e1.name().compareToIgnoreCase(e2.name());
            case "name_desc":
                return e2.name().compareToIgnoreCase(e1.name());
            case "episode_code":
                return e1.episodeCode().compareTo(e2.episodeCode());
            case "episode_code_desc":
                return e2.episodeCode().compareTo(e1.episodeCode());
            default:
                return 0;
        }
    }

    private InfoDto rewriteInfoDto(InfoDto originalInfo) {
        String nextUrl = Optional.ofNullable(originalInfo.next())
                .map(next -> next.replace(config.getApiBaseUrl()+ "/episode",
                        config.getLocalBaseUrl() + "/episodes"))
                .orElse(null);
    
        String prevUrl = Optional.ofNullable(originalInfo.prev())
                .map(prev -> prev.replace(config.getApiBaseUrl() + "/episode",
                        config.getLocalBaseUrl() + "/episodes"))
                .orElse(null);
    
        return new InfoDto(
                originalInfo.count(),
                originalInfo.pages(),
                nextUrl,
                prevUrl);
    }

    private EpisodeDto rewriteEpisodeDto(EpisodeDto episode) {
        return new EpisodeDto(
                episode.id(),
                episode.name(),
                episode.episodeCode(),
                episode.releaseDate(),
                episode.characters().stream()
                        .map(character -> character.replace(config.getApiBaseUrl() + "/character/",
                                config.getLocalBaseUrl() + "/characters/"))
                        .collect(Collectors.toList()));
    }
}