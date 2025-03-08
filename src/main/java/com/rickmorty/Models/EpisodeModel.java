package com.rickmorty.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "episodes")
public class EpisodeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
