package com.rickmorty.Controllers;

import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.EpisodeDto;
import com.rickmorty.Services.EpisodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/episodes")
public class EpisodeController {

    @Autowired
    private EpisodeService episodeService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<EpisodeDto>> getAllEpisodes(
        @RequestParam(required = false) Integer page) {
        ApiResponseDto<EpisodeDto> episodes = episodeService.findAllEpisodes(page);
        return ResponseEntity.ok(episodes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EpisodeDto> getEpisodeById(@PathVariable String id) {
        EpisodeDto episode = episodeService.findEpisodeById(id);
        return new ResponseEntity<>(episode, HttpStatus.OK);
    }
}