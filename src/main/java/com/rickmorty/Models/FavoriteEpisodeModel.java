package com.rickmorty.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("EPISODE")
@Table(name = "favorite_episodes")
public class FavoriteEpisodeModel extends FavoriteModel {
    @ManyToOne
    @JoinColumn(name = "episode_id", nullable = false)
    private EpisodeModel episode;
}
