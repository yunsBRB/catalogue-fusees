package be.technofutur.exospring.models.fusee;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FuseeForm {
    @NotBlank
    @Size(max = 60)
    private String nom;
    @NotBlank
    @Size(max = 500)
    private String description;
    @Size(max = 500)
    @Pattern(regexp = "^(|/images/[a-zA-Z0-9._/-]+|https://[^\\s]+)$", message = "Utilisez une adresse HTTPS ou /images/nom.jpg")
    private String imageUrl;
}
