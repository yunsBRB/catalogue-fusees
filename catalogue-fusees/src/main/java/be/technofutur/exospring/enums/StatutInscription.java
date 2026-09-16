package be.technofutur.exospring.enums;

public enum StatutInscription {
    EN_ATTENTE("Dépôt en attente"),
    DEPOSEE("Déposé sur la Lune");

    private final String libelle;

    StatutInscription(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
