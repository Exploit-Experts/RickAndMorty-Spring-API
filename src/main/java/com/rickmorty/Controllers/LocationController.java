package com.rickmorty.Controllers;

import com.rickmorty.DTO.LocationDto;
import com.rickmorty.Models.LocationModel;
import com.rickmorty.Services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<LocationDto> getAllLocations() {
        return locationService.findAllLocations();
    }
}
