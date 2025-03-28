package com.rickmorty.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("CHARACTER")
@Table(name = "favorite_characters")
public class FavoriteCharacterModel extends FavoriteModel {
    @ManyToOne
    @JoinColumn(name = "character_id", nullable = false)
    private CharacterModel character;
}
