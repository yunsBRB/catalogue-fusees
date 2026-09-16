package be.technofutur.exospring.models.panier;

import java.math.BigDecimal;
import java.util.List;

public record PanierDto(List<LignePanierDto> lignes) {
    public BigDecimal total() {
        return lignes.stream().map(LignePanierDto::prix).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
