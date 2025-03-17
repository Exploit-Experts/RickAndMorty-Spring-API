package com.rickmorty.exceptions;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException() {
        super("Localização não encontrada");
    }
}
