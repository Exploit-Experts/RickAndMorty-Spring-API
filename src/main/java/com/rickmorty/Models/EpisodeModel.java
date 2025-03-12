package com.rickmorty.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "episodes")
public class EpisodeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "air_date", nullable = false)
    private LocalDate airDate;

    @Column(name = "episode_code", nullable = false, length = 20)
    private String episodeCode;

    @ManyToMany(mappedBy = "episodes", fetch = FetchType.LAZY)
    private List<CharacterModel> characters;

}
