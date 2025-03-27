package com.rickmorty.interfaces;

import com.rickmorty.dtos.ApiResponseDto;
import com.rickmorty.dtos.LocationDto;
import com.rickmorty.enums.SortLocation;

public interface LocationServiceInterface {
  ApiResponseDto<LocationDto> findAllLocations(Integer page, String name, String type, String dimension,
      SortLocation sort);

  LocationDto getLocationById(Long id);
}
