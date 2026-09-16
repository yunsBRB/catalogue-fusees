package be.technofutur.exospring.enums;

public enum StatutCommande {
    CONFIRMEE("Confirmée");

    private final String libelle;

    StatutCommande(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
