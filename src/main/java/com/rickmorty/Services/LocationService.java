package com.rickmorty.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.InfoDto;
import com.rickmorty.DTO.LocationDto;
import com.rickmorty.Models.LocationModel;
import com.rickmorty.Repository.LocationRepository;
import com.rickmorty.Utils.Config;
import com.rickmorty.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.rickmorty.enums.SortLocation;
import com.rickmorty.interfaces.LocationServiceInterface;

@Slf4j
@Service
public class LocationService implements LocationServiceInterface {

    private final Config config;
    private final ObjectMapper objectMapper;
    private final LocationRepository locationRepository;

    public LocationService(ObjectMapper objectMapper, LocationRepository locationRepository, Config config) {
        this.objectMapper = objectMapper;
        this.locationRepository = locationRepository;
        this.config = config;
    }

    @Override
    public ApiResponseDto<LocationDto> findAllLocations(Integer page, String name, String type, String dimension, SortLocation sort) {
        if (page != null && page < 1) throw new InvalidParameterException("Parâmetro page incorreto, deve ser um numero inteiro maior ou igual a 1");

        try {

            int pageNumber = page != null ? page - 1 : 0;
            int pageSize = 20;
            Pageable pageable = createPageable(pageNumber, pageSize, sort);


            Page<LocationModel> locationPage;
            if (name != null || type != null || dimension != null) {
                locationPage = locationRepository.findWithFilters(
                        name, type, dimension, pageable);
            } else {
                locationPage = locationRepository.findAll(pageable);
            }


            List<LocationDto> locations = locationPage.getContent().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());


            InfoDto info = new InfoDto(
                    (int) locationPage.getTotalElements(),
                    locationPage.getTotalPages(),
                    locationPage.hasNext() ? buildNextUrl(pageNumber + 1, name, type, dimension) : null,
                    locationPage.hasPrevious() ? buildPrevUrl(pageNumber - 1, name, type, dimension) : null
            );

            return new ApiResponseDto<>(info, locations);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public LocationDto getLocationById(Long id) {
        if (id == null || id < 1) throw new InvalidIdException();

        try {
            Optional<LocationModel> locationOpt = locationRepository.findById(id);
            if (locationOpt.isEmpty()) {
                throw new LocationNotFoundException("Localização não encontrada");
            }

            return convertToDto(locationOpt.get());
        } catch (LocationNotFoundException ex) {
            throw new LocationNotFoundException(ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Pageable createPageable(int page, int size, SortLocation sort) {
        if (sort == null) {
            return PageRequest.of(page, size);
        }

        Sort.Direction direction;
        String property;

        switch (sort) {
            case NAME_ASC:
                direction = Sort.Direction.ASC;
                property = "name";
                break;
            case NAME_DESC:
                direction = Sort.Direction.DESC;
                property = "name";
                break;
            case TYPE_ASC:
                direction = Sort.Direction.ASC;
                property = "locationType";
                break;
            case TYPE_DESC:
                direction = Sort.Direction.DESC;
                property = "locationType";
                break;
            case DIMENSION_ASC:
                direction = Sort.Direction.ASC;
                property = "dimension";
                break;
            case DIMENSION_DESC:
                direction = Sort.Direction.DESC;
                property = "dimension";
                break;
            default:
                return PageRequest.of(page, size);
        }

        return PageRequest.of(page, size, Sort.by(direction, property));
    }

    private LocationDto convertToDto(LocationModel model) {
        return new LocationDto(
                model.getId(),
                model.getName(),
                model.getLocationType(),
                model.getDimension(),


                List.of(),
                config.getLocalBaseUrl() + "/locations/" + model.getId()
        );
    }

    private String buildNextUrl(int nextPage, String name, String type, String dimension) {
        StringBuilder url = new StringBuilder(config.getLocalBaseUrl() + "/locations?page=" + (nextPage + 1));
        if (name != null) url.append("&name=").append(name);
        if (type != null) url.append("&type=").append(type);
        if (dimension != null) url.append("&dimension=").append(dimension);
        return url.toString();
    }

    private String buildPrevUrl(int prevPage, String name, String type, String dimension) {
        StringBuilder url = new StringBuilder(config.getLocalBaseUrl() + "/locations?page=" + (prevPage + 1));
        if (name != null) url.append("&name=").append(name);
        if (type != null) url.append("&type=").append(type);
        if (dimension != null) url.append("&dimension=").append(dimension);
        return url.toString();
    }

    public LocationModel saveLocationByDto(LocationDto dto) {
        Optional<LocationModel> locationOpt = locationRepository.findById(dto.id());

        if (locationOpt.isEmpty()) {
            LocationModel model = new LocationModel();
            model.setName(dto.name());
            model.setDimension(dto.dimension());
            model.setLocationType(dto.type());
            model.setCharacters(null);
            model.setId(dto.id());
            return locationRepository.save(model);
        }

        return null;
    }
    
}