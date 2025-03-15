package com.rickmorty.Models;

import com.rickmorty.enums.Gender;
import com.rickmorty.enums.LifeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "characters")
public class CharacterModel {

    @Id
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "character_status", nullable = false)
    private LifeStatus status;

    @Column(nullable = false, length = 50)
    private String species;

    @Column(name = "character_type", nullable = false, length = 50)
    private String characterType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationModel locationModel;

    @ManyToMany
    @JoinTable(
            name = "character_episodes",
            joinColumns = @JoinColumn(name = "character_id"),
            inverseJoinColumns = @JoinColumn(name = "episode_id")
    )
    private List<EpisodeModel> episodes;

    @Column(name = "is_avatar_uploaded")
    private boolean avatarUploaded = false;

}