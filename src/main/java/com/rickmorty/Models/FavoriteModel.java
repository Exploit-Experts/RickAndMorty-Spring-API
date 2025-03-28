package com.rickmorty.Models;

import com.rickmorty.enums.FavoriteTypes;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "favorites")
public abstract class FavoriteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Enumerated(EnumType.STRING)
    @Column(name = "favorite_type", nullable = false, length = 20)
    private FavoriteTypes favoriteType;
}
