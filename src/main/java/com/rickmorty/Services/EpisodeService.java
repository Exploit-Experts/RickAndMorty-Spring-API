package com.rickmorty.Services;

import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.EpisodeDto;
import com.rickmorty.DTO.InfoDto;
import com.rickmorty.Models.EpisodeModel;
import com.rickmorty.Repository.EpisodeRepository;
import com.rickmorty.Utils.Config;
import com.rickmorty.enums.SortEpisode;
import com.rickmorty.exceptions.EpisodeNotFoundException;
import com.rickmorty.exceptions.InvalidIdException;
import com.rickmorty.exceptions.InvalidParameterException;
import com.rickmorty.exceptions.NotFoundException;
import com.rickmorty.interfaces.EpisodeServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EpisodeService implements EpisodeServiceInterface {

    @Autowired
    private Config config;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Override
    public ApiResponseDto<EpisodeDto> findAllEpisodes(Integer page, String name, String episode, SortEpisode sort) {
        if (episode != null && !Pattern.matches("^S\\d{2}(E\\d{2})?$", episode.toUpperCase())) {
            throw new InvalidParameterException("Parâmetro episode não está no formato correto. Esperado: SXXEXX");
        }

        try {
            if (page != null && page <= 0) {
                throw new InvalidParameterException("Parâmetro page incorreto, deve ser um número inteiro maior ou igual a 1");
            }

            int pageNumber = page != null ? page - 1 : 0; // Spring Data JPA usa indexação baseada em 0
            Pageable pageable = PageRequest.of(pageNumber, 20); // 20 itens por página

            Page<EpisodeModel> episodePage;
            if (name != null && episode != null) {
                episodePage = episodeRepository.findByNameContainingIgnoreCaseAndEpisodeCodeContainingIgnoreCase(name, episode, pageable);
            } else if (name != null) {
                episodePage = episodeRepository.findByNameContainingIgnoreCase(name, pageable);
            } else if (episode != null) {
                episodePage = episodeRepository.findByEpisodeCodeContainingIgnoreCase(episode, pageable);
            } else {
                episodePage = episodeRepository.findAll(pageable);
            }

            if (episodePage.isEmpty()) {
                throw new NotFoundException();
            }

            List<EpisodeDto> episodeDtos = episodePage.getContent().stream()
                    .map(this::convertToDto)
                    .sorted((e1, e2) -> compareEpisodes(e1, e2, String.valueOf(sort)))
                    .collect(Collectors.toList());

            InfoDto infoDto = new InfoDto(
                    episodePage.getTotalElements(), // long
                    episodePage.getTotalPages(), // long
                    episodePage.hasNext() ? String.valueOf(page + 1) : null,
                    episodePage.hasPrevious() ? String.valueOf(page - 1) : null
            );

            return new ApiResponseDto<>(infoDto, episodeDtos);

        } catch (InvalidParameterException e) {
            throw new InvalidParameterException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            log.error("Erro ao buscar episódios: " + e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar episódios", e);
        }
    }

    @Override
    public EpisodeDto findEpisodeById(Long id) {
        try {
            if (id == null || id < 1) {
                throw new InvalidIdException();
            }

            Optional<EpisodeModel> episodeOptional = episodeRepository.findById(id);
            if (episodeOptional.isEmpty()) {
                throw new EpisodeNotFoundException();
            }

            return convertToDto(episodeOptional.get());

        } catch (InvalidIdException e) {
            throw new InvalidIdException();
        } catch (EpisodeNotFoundException e) {
            throw new EpisodeNotFoundException();
        } catch (Exception e) {
            log.error("Erro ao buscar episódio por ID: " + e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar episódio por ID", e);
        }
    }

    private EpisodeDto convertToDto(EpisodeModel episode) {
        return new EpisodeDto(
                episode.getId().intValue(),
                episode.getName(),
                episode.getEpisodeCode(),
                episode.getAirDate().toString(),
                episode.getCharacters().stream()
                        .map(character -> config.getLocalBaseUrl() + "/characters/" + character.getId())
                        .collect(Collectors.toList())
        );
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
}