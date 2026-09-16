package be.technofutur.exospring.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Fusee extends BaseEntity {
    @Column(nullable = false, length = 60)
    private String nom;
    @Column(nullable = false, length = 500)
    private String description;
    @Column(length = 500)
    private String imageUrl;
}
