package com.rickmorty.interfaces;

import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.CharacterDto;
import com.rickmorty.enums.Gender;
import com.rickmorty.enums.LifeStatus;
import com.rickmorty.enums.SortOrder;
import com.rickmorty.enums.Species;
import org.springframework.http.ResponseEntity;

public interface CharacterServiceInterface {
  ApiResponseDto<CharacterDto> findAllCharacters(Integer page, String name, LifeStatus status, Species species,
      String type, Gender gender, SortOrder sort);

  CharacterDto findACharacterById(Long id);

  ResponseEntity<byte[]> findCharacterAvatar(Long id);
}
