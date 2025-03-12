package com.rickmorty.Services;

import com.rickmorty.Repository.CharacterRepository;
import com.rickmorty.Repository.EpisodeRepository;
import com.rickmorty.Repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SyncApiService {

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    private final RestTemplate restTemplate;

    public SyncApiService() {
        restTemplate = new RestTemplate();
    }
}
