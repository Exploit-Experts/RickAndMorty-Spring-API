package com.rickmorty.interfaces;

import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.LocationDto;
import com.rickmorty.enums.SortLocation;

public interface LocationServiceInterface {
  ApiResponseDto<LocationDto> findAllLocations(Integer page, String name, String type, String dimension,
      SortLocation sort);

  LocationDto getLocationById(Long id);
}
