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

    private final FavoriteRepository favoriteRepository;
    private final FavoriteCharacterRepository favoriteCharacterRepository;
    private final FavoriteEpisodeRepository favoriteEpisodeRepository;
    private final FavoriteLocationRepository favoriteLocationRepository;
    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;
    private final EpisodeRepository episodeRepository;
    private final LocationRepository locationRepository;

    private final TokenService tokenService;
    private final CharacterService characterService;
    private final EpisodeService episodeService;
    private final LocationService locationService;

    public FavoriteService(
        FavoriteRepository favoriteRepository,
        FavoriteCharacterRepository favoriteCharacterRepository,
        FavoriteEpisodeRepository favoriteEpisodeRepository,
        FavoriteLocationRepository favoriteLocationRepository,
        UserRepository userRepository,
        TokenService tokenService,
        CharacterRepository characterRepository,
        EpisodeRepository episodeRepository,
        LocationRepository locationRepository,
        CharacterService characterService,
        EpisodeService episodeService,
        LocationService locationService
    ) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteCharacterRepository = favoriteCharacterRepository;
        this.favoriteEpisodeRepository = favoriteEpisodeRepository;
        this.favoriteLocationRepository = favoriteLocationRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.characterRepository = characterRepository;
        this.episodeRepository = episodeRepository;
        this.locationRepository = locationRepository;
        this.characterService = characterService;
        this.episodeService = episodeService;
        this.locationService = locationService;
    }

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
    public Page<?> getAllFavoritesByUserId(
        String token,
        Long userId,
        FavoriteTypes favoriteType,
        int page,
        SortFavorite sort,
        Sort.Direction sortDirection
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sortDirection, String.valueOf(sort)));
        Long resolvedUserId = resolveUserId(token, userId);

        return switch (favoriteType) {
            case CHARACTER -> getAllCharactersFavorites(resolvedUserId, pageable);
            case EPISODE -> getAllEpisodesFavorites(resolvedUserId, pageable);
            case LOCATION -> getAllLocationsFavorites(resolvedUserId, pageable);
        };
    }

    @Override
    public Page<?> getAllFavorites(
        int page,
        SortFavorite sort,
        FavoriteTypes favoriteType,
        Sort.Direction sortDirection
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sortDirection, String.valueOf(sort)));

        return switch (favoriteType) {
            case CHARACTER -> getAllCharactersFavorites(null, pageable);
            case EPISODE -> getAllEpisodesFavorites(null, pageable);
            case LOCATION -> getAllLocationsFavorites(null, pageable);
        };
    }

    public Object getFavoriteById(Long userId, Long favoriteId) {
        FavoriteModel favorite = userId == null
            ? favoriteRepository.findById(favoriteId).orElseThrow(FavoriteNotFound::new)
            : favoriteRepository.findByUserFavoriteId(userId, favoriteId);

        if (favorite == null) throw new FavoriteNotFound();

        return switch (favorite.getFavoriteType()) {
            case CHARACTER ->
                characterService.convertCharacterToDto(((FavoriteCharacterModel) favorite).getCharacter());
            case EPISODE -> episodeService.convertEpisodeToDto(((FavoriteEpisodeModel) favorite).getEpisode());
            case LOCATION -> locationService.convertLocationToDto(((FavoriteLocationModel) favorite).getLocation());
        };
    }

    @Override
    public void removeFavorite(Long favoriteId) {
        FavoriteModel favorite = favoriteRepository.findById(favoriteId)
            .orElseThrow(FavoriteNotFound::new);
        favoriteRepository.delete(favorite);
    }

    @Override
    public void removeAllFavoritesByUserId(String token, Long userId) {
        Long resolvedUserId = resolveUserId(token, userId);
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
        Page<FavoriteCharacterModel> favoritesPage = (userId == null)
            ? favoriteCharacterRepository.findAll(pageable)
            : favoriteCharacterRepository.findByUserId(userId, pageable);

        return favoritesPage.map(favorite -> {
            CharacterModel character = favorite.getCharacter();
            return characterService.convertCharacterToDto(character);
        });
    }

    private Page<LocationResponseDto> getAllLocationsFavorites(Long userId, Pageable pageable) {
        Page<FavoriteLocationModel> favoritesPage = (userId == null)
            ? favoriteLocationRepository.findAll(pageable)
            : favoriteLocationRepository.findByUserId(userId, pageable);

        if (favoritesPage.isEmpty()) throw new FavoriteNotFound();

        return favoritesPage.map(favorite -> {
            LocationModel location = favorite.getLocation();
            return locationService.convertLocationToDto(location);
        });
    }

    private Page<EpisodeResponseDto> getAllEpisodesFavorites(Long userId, Pageable pageable) {
        Page<FavoriteEpisodeModel> favoritesPage = (userId == null)
            ? favoriteEpisodeRepository.findAll(pageable)
            : favoriteEpisodeRepository.findByUserId(userId, pageable);

        if (favoritesPage.isEmpty()) throw new FavoriteNotFound();

        return favoritesPage.map(favorite -> {
            EpisodeModel episode = favorite.getEpisode();
            return episodeService.convertEpisodeToDto(episode);
        });
    }
}