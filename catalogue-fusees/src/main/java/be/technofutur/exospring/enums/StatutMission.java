package be.technofutur.exospring.enums;

public enum StatutMission {
    PLANIFIEE("Planifiée"),
    EN_VOL("En vol"),
    TERMINEE("Terminée"),
    ANNULEE("Annulée");

    private final String libelle;

    StatutMission(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
