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
public class locationService implements LocationServiceInterface {

    private final Config config;
    private final ObjectMapper objectMapper;
    private final LocationRepository locationRepository;

    public LocationService(ObjectMapper objectMapper, LocationRepository locationRepository, Config config) {
        this.objectMapper = objectMapper;
        this.locationRepository = locationRepository;
        this.config = config;
    }

    public locationService(Config config, ObjectMapper objectMapper, LocationRepository locationRepository) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.locationRepository = locationRepository;
    }

    @Override
    public ApiResponseDto<LocationDto> findAllLocations(Integer page, String name, String type, String dimension, SortLocation sort) {
        log.debug("Iniciando busca por locais - page: {}, name: {}, type: {}, dimension: {}, sort: {}",
                page, name, type, dimension, sort);

        if (page != null && page < 1) {
            log.error("Parâmetro page inválido: {}", page);
            throw new InvalidParameterException("Parâmetro page incorreto, deve ser um numero inteiro maior ou igual a 1");
        }

        try {
            int pageNumber = page != null ? page - 1 : 0;
            int pageSize = 20;
            Pageable pageable = createPageable(pageNumber, pageSize, sort);

            log.debug("Criado pageable: {}", pageable);

            Page<LocationModel> locationPage;
            if (name != null || type != null || dimension != null) {
                log.debug("Aplicando filtros - name: {}, type: {}, dimension: {}", name, type, dimension);
                locationPage = locationRepository.findWithFilters(name, type, dimension, pageable);
            } else {
                log.debug("Buscando todos os locais sem filtros");
                locationPage = locationRepository.findAll(pageable);
            }

            if (locationPage.isEmpty()) {
                log.warn("Nenhum local encontrado com os filtros aplicados");
                throw new NotFoundException();
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

            log.info("Busca concluída - {} locais encontrados", locations.size());
            return new ApiResponseDto<>(info, locations);
        } catch (NotFoundException e) {
            log.error("Nenhum local encontrado", e);
            throw new NotFoundException();
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar locais", e);
            throw new RuntimeException("Erro ao processar requisição");
        }
    }


    @Override
    public LocationDto getLocationById(Long id) {
        log.debug("Buscando local por ID: {}", id);

        if (id == null || id < 1) {
            log.error("ID inválido: {}", id);
            throw new InvalidIdException();
        }

        try {
            Optional<LocationModel> locationOpt = locationRepository.findById(id);

            if (locationOpt.isEmpty()) {
                log.warn("Local não encontrado para ID: {}", id);
                throw new LocationNotFoundException("Localização não encontrada");
            }

            LocationDto dto = convertToDto(locationOpt.get());
            log.info("Local encontrado para ID {}: {}", id, dto.name());
            return dto;
        } catch (LocationNotFoundException ex) {
            log.error("Erro ao buscar local por ID: {}", id, ex);
            throw new LocationNotFoundException(ex.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar local por ID: {}", id, e);
            throw new RuntimeException("Erro ao processar requisição");
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
        log.trace("Convertendo LocationModel para DTO: {}", model.getId());

        List<String> residents = model.getCharacters() != null ?
                model.getCharacters().stream()
                        .map(character -> {
                            log.trace("Mapeando residente: {}", character.getId());
                            return config.getLocalBaseUrl() + "/characters/" + character.getId();
                        })
                        .collect(Collectors.toList()) :
                List.of();

        return new LocationDto(
                model.getId(),
                model.getName(),
                model.getLocationType(),
                model.getDimension(),
                residents,
                config.getLocalBaseUrl() + "/locations/" + model.getId()
        );
    }
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

public void main() {
}