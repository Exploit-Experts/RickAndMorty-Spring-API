package com.rickmorty.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "characters")
public class CharacterModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}