package com.rickmorty.interfaces;

import com.rickmorty.DTO.FavoriteDto;
import com.rickmorty.DTO.FavoriteResponseDto;
import com.rickmorty.DTO.responses.CharacterResponseDto;
import com.rickmorty.enums.FavoriteTypes;
import com.rickmorty.enums.SortFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;

public interface FavoriteServiceInterface {
    void create(String token, FavoriteDto favoriteDto, BindingResult result);

    Page<?> getAllFavoritesByUserId(String token, Long userId, FavoriteTypes favoriteType, int page, SortFavorite sort, Sort.Direction sortDirection);

    Page<?> getAllFavorites(int page, SortFavorite sort, FavoriteTypes favoriteType, Sort.Direction sortDirection);

    void removeFavorite(Long favoriteId);

    void removeAllFavoritesByUserId(String token, Long userId);
}