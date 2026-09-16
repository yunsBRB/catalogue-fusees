package be.technofutur.exospring.initializers;

import be.technofutur.exospring.entities.*;
import be.technofutur.exospring.enums.*;
import be.technofutur.exospring.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.demo", havingValue = "true")
public class Seed implements CommandLineRunner {
    private final UtilisateurRepository utilisateurs;
    private final FuseeRepository fusees;
    private final MissionRepository missions;
    private final CommandeRepository commandes;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (utilisateurs.count() > 0) return;
        Utilisateur client = utilisateur("client", "Client123!", Role.CLIENT);
        Utilisateur nora = utilisateur("nora", "Nora123!", Role.ASTRONAUTE);
        utilisateur("admin", "Admin123!", Role.ADMIN);
        Fusee saturn = fusee("Saturn IB", "Lanceur du programme Apollo. L’étage S-IVB figure sur la photographie.");
        Fusee saturnV = fusee("Saturn V", "Lanceur des missions lunaires Apollo.");
        mission("Séléné 01", saturn, nora, LocalDate.now().plusDays(90), 24, StatutMission.PLANIFIEE);
        mission("Séléné 02", saturnV, nora, LocalDate.now().plusDays(150), 0, StatutMission.PLANIFIEE);
        Mission enVol = mission("Séléné 00", saturn, nora, LocalDate.now().minusDays(2), 0, StatutMission.EN_VOL);
        Commande c = new Commande();
        c.setUtilisateur(client);
        c.setStatut(StatutCommande.CONFIRMEE);
        LigneCommande l = new LigneCommande();
        l.setCommande(c);
        l.setMission(enVol);
        l.setNomInscrit("Younes");
        l.setPrix(new BigDecimal("29.00"));
        l.setStatut(StatutInscription.EN_ATTENTE);
        c.getLignes().add(l);
        commandes.save(c);
    }

    private Utilisateur utilisateur(String nom, String password, Role role) {
        Utilisateur u = new Utilisateur();
        u.setUsername(nom);
        u.setEmail(nom + "@example.com");
        u.setPassword(encoder.encode(password));
        u.setRole(role);
        return utilisateurs.save(u);
    }

    private Fusee fusee(String nom, String description) {
        Fusee f = new Fusee();
        f.setNom(nom);
        f.setDescription(description);
        f.setImageUrl("");
        return fusees.save(f);
    }

    private Mission mission(String nom, Fusee fusee, Utilisateur astronaute, LocalDate date, int places, StatutMission statut) {
        Mission m = new Mission();
        m.setNom(nom);
        m.setFusee(fusee);
        m.setAstronaute(astronaute);
        m.setDateDepart(date);
        m.setPrix(new BigDecimal("29.00"));
        m.setPlacesDisponibles(places);
        m.setStatut(statut);
        return missions.save(m);
    }
}
