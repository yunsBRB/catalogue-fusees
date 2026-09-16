package be.technofutur.exospring.models.auth;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InscriptionForm {
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9._-]{3,30}", message = "3 à 30 lettres, chiffres, points, tirets ou underscores")
    private String username;
    @NotBlank
    @Email
    @Size(max = 150)
    private String email;
    @NotBlank
    @Size(min = 8, max = 64)
    private String password;
}
