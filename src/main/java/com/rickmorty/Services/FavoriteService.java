package com.rickmorty.Services;

import com.rickmorty.DTO.FavoriteResponseDto;
import com.rickmorty.DTO.responses.CharacterResponseDto;
import com.rickmorty.DTO.responses.EpisodeResponseDto;
import com.rickmorty.DTO.responses.LocationResponseDto;
import com.rickmorty.Models.*;
import com.rickmorty.Repository.*;
import com.rickmorty.enums.FavoriteTypes;
import com.rickmorty.enums.SortFavorite;
import com.rickmorty.enums.UserRole;
import com.rickmorty.exceptions.*;
import com.rickmorty.interfaces.FavoriteServiceInterface;
import com.rickmorty.DTO.FavoriteDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.*;

@Slf4j
@Service
public class FavoriteService implements FavoriteServiceInterface {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private FavoriteCharacterRepository favoriteCharacterRepository;

    @Autowired
    private FavoriteLocationRepository favoriteLocationRepository;

    @Autowired
    private FavoriteEpisodeRepository favoriteEpisodeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public void create(String token, FavoriteDto favoriteDto, BindingResult result) {
        Long favoriteId = favoriteDto.favoriteId();
        UserModel user = userRepository.findByIdAndActive(tokenService.extractUserId(token), 1)
                .orElseThrow(UserNotFoundException::new);

        switch (favoriteDto.favoriteType()) {
            case FavoriteTypes.CHARACTER -> saveCharacterFavorite(favoriteId, user);
            case FavoriteTypes.EPISODE -> saveEpisodeFavorite(favoriteId, user);
            case FavoriteTypes.LOCATION -> saveLocationFavorite(favoriteId, user);
        }
    }

    @Override
    public Page<?> getAllFavorites(
            String token,
            Long userId,
            FavoriteTypes favoriteType,
            int page,
            SortFavorite sort,
            Sort.Direction sortDirection
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sortDirection, String.valueOf(sort)));
        Long resolvedUserId = resolveUserId(token, userId);

        switch (favoriteType){
            case CHARACTER -> {
                return getAllCharactersFavorites(resolvedUserId, pageable);
            }
            case EPISODE -> {
                return getAllEpisodesFavorites(resolvedUserId, pageable);
            }
            case LOCATION -> {
                return getAllLocationsFavorites(resolvedUserId, pageable);
            }
            default -> {
                throw new InvalidParameterException("Invalid favorite type");
            }
        }
    }

    @Override
    public void removeFavorite(String token, Long userId, Long favoriteId) {
        Long resolvedUserId = resolveUserId(token, userId);

        Long existsFavorite = favoriteRepository.existsByUserIdAndFavoriteId(resolvedUserId, favoriteId);
        if (existsFavorite == 0) throw new FavoriteNotFound();

        favoriteRepository.deleteByUserIdAndFavoriteId(resolvedUserId, favoriteId);
    }

    @Override
    public void removeAllFavoritesByUserId(String token, Long userId) {
        Long resolvedUserId = resolveUserId(token, userId);

        Long existsFavorite = favoriteRepository.existsByUserId(resolvedUserId);
        if (existsFavorite == 0) throw new FavoriteNotFound();

        favoriteRepository.deleteAllByUserId(resolvedUserId);
    }

    private void saveCharacterFavorite(Long characterId, UserModel user) {
        FavoriteCharacterModel favorite = new FavoriteCharacterModel();
        CharacterModel character = characterRepository.findById(characterId)
                .orElseThrow(CharacterNotFoundException::new);

        favorite.setUser(user);
        favorite.setCharacter(character);
        characterRepository.save(character);
    }

    private void saveEpisodeFavorite(Long episodeId, UserModel user) {
        FavoriteEpisodeModel favorite = new FavoriteEpisodeModel();
        EpisodeModel episode = episodeRepository.findById(episodeId)
                .orElseThrow(EpisodeNotFoundException::new);

        favorite.setUser(user);
        favorite.setEpisode(episode);
        episodeRepository.save(episode);
    }

    private void saveLocationFavorite(Long locationId, UserModel user) {
        FavoriteLocationModel favorite = new FavoriteLocationModel();
        LocationModel location = locationRepository.findById(locationId)
                .orElseThrow(LocationNotFoundException::new);

        favorite.setUser(user);
        favorite.setLocation(location);
        locationRepository.save(location);
    }

    private Long resolveUserId(String token, Long userId) {
        UserModel user = userRepository.findByIdAndActive(tokenService.extractUserId(token), 1)
                .orElseThrow(UserNotFoundException::new);

        return user.getRole() == UserRole.USER
                ? user.getId()
                : userId;
    }

    private Page<CharacterResponseDto> getAllCharactersFavorites(Long userId, Pageable pageable) {
        Page<FavoriteCharacterModel> favoritesPage = favoriteCharacterRepository.findByUserId(userId, pageable);
        if (favoritesPage.isEmpty()) throw new FavoriteNotFound();

        return favoritesPage.map(favorite -> {
            CharacterModel character = favorite.getCharacter();
            return new CharacterResponseDto(
                    character.getId(),
                    character.getName(),
                    character.getStatus(),
                    character.getSpecies(),
                    character.getCharacterType(),
                    character.getGender(),
                    character.getLocationModel()
            );
        });
    }

    private Page<LocationResponseDto> getAllLocationsFavorites(Long userId, Pageable pageable) {
        Page<FavoriteLocationModel> favoritesPage = favoriteLocationRepository.findByUserId(userId, pageable);
        if (favoritesPage.isEmpty()) throw new FavoriteNotFound();

        return favoritesPage.map(favorite -> {
            LocationModel location = favorite.getLocation();
            return new LocationResponseDto(
                    location.getId(),
                    location.getName(),
                    location.getLocationType(),
                    location.getDimension(),
                    location.getCharacters()
            );
        });
    }

    private Page<EpisodeResponseDto> getAllEpisodesFavorites(Long userId, Pageable pageable) {
        Page<FavoriteEpisodeModel> favoritesPage = favoriteEpisodeRepository.findByUserId(userId, pageable);
        if (favoritesPage.isEmpty()) throw new FavoriteNotFound();

        return favoritesPage.map(favorite -> {
            EpisodeModel episode = favorite.getEpisode();
            return new EpisodeResponseDto(
                    episode.getId(),
                    episode.getName(),
                    episode.getEpisodeCode(),
                    episode.getAirDate(),
                    episode.getCharacters()
            );
        });
    }
}