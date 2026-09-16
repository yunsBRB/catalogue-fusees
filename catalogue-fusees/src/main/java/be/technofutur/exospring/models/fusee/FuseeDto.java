package be.technofutur.exospring.models.fusee;

import be.technofutur.exospring.entities.Fusee;

public record FuseeDto(Long id, String nom, String description, String imageUrl) {
    public static FuseeDto fromEntity(Fusee f) {
        return new FuseeDto(f.getId(), f.getNom(), f.getDescription(), f.getImageUrl());
    }
}
