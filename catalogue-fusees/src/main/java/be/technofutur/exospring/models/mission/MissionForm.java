package be.technofutur.exospring.models.mission;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissionForm {
    @NotBlank
    @Size(max = 60)
    private String nom;
    @NotNull
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
    private java.time.LocalDate dateDepart;
    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 8, fraction = 2)
    private java.math.BigDecimal prix;
    @NotNull
    @Min(0)
    @Max(10000)
    private Integer placesDisponibles;
    @NotNull
    private Long fuseeId;
    @NotNull
    private Long astronauteId;
    @NotNull
    private be.technofutur.exospring.enums.StatutMission statut = be.technofutur.exospring.enums.StatutMission.PLANIFIEE;
}
