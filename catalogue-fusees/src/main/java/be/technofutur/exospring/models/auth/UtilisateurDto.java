package be.technofutur.exospring.models.auth;

import be.technofutur.exospring.entities.Utilisateur;

public record UtilisateurDto(Long id, String username) {
    public static UtilisateurDto fromEntity(Utilisateur u) {
        return new UtilisateurDto(u.getId(), u.getUsername());
    }
}
