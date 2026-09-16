package be.technofutur.exospring.models.panier;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AjoutPanierForm {
    @NotBlank
    @Size(max = 40)
    private String nomInscrit;
}
