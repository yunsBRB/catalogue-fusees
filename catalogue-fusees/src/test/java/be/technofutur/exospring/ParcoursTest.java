package be.technofutur.exospring;

import be.technofutur.exospring.entities.*;
import be.technofutur.exospring.enums.*;
import be.technofutur.exospring.models.panier.AjoutPanierForm;
import be.technofutur.exospring.repositories.*;
import be.technofutur.exospring.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:tests;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop", "app.demo=false"
})
@AutoConfigureMockMvc
@Transactional
class ParcoursTest {
    @Autowired MockMvc mvc;
    @Autowired UtilisateurRepository utilisateurs;
    @Autowired FuseeRepository fusees;
    @Autowired MissionRepository missions;
    @Autowired CommandeRepository commandes;
    @Autowired LigneCommandeRepository billets;
    @Autowired PanierService panier;
    @Autowired CommandeService commandeService;
    @Autowired MissionService missionService;

    Utilisateur client;
    Utilisateur autre;
    Utilisateur admin;
    Utilisateur nora;
    Mission mission;
    Fusee fusee;

    @BeforeEach
    void preparer() {
        client = compte("client", Role.CLIENT);
        autre = compte("autre", Role.CLIENT);
        admin = compte("admin", Role.ADMIN);
        nora = compte("nora", Role.ASTRONAUTE);
        fusee = new Fusee();
        fusee.setNom("Saturn");
        fusee.setDescription("Lanceur");
        fusees.save(fusee);
        mission = new Mission();
        mission.setNom("Séléné");
        mission.setFusee(fusee);
        mission.setAstronaute(nora);
        mission.setPrix(new BigDecimal("29.00"));
        mission.setPlacesDisponibles(2);
        mission.setDateDepart(LocalDate.now().plusDays(30));
        mission.setStatut(StatutMission.PLANIFIEE);
        missions.save(mission);
    }

    @Test
    void pagesPubliquesEtFormulaires() throws Exception {
        for (String url : new String[]{"/", "/fusees", "/fusees/" + fusee.getId(),
                "/missions", "/missions/" + mission.getId(), "/login", "/inscription"}) {
            mvc.perform(get(url)).andExpect(status().isOk());
        }
        for (String url : new String[]{"/admin/fusees/ajouter", "/admin/fusees/" + fusee.getId() + "/modifier",
                "/admin/missions/ajouter", "/admin/missions/" + mission.getId() + "/modifier"}) {
            mvc.perform(get(url).with(user(admin))).andExpect(status().isOk());
        }
        mvc.perform(get("/missions").param("nom", "inconnu")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Aucune mission")));
        mvc.perform(get("/missions/999999")).andExpect(status().isNotFound());
    }

    @Test
    void achatConserveLePrixEtVideLePanier() throws Exception {
        ajouter("Younes");
        mvc.perform(get("/panier").with(user(client))).andExpect(status().isOk());
        Long id = commandeService.confirmer(client.getId());
        assertThat(mission.getPlacesDisponibles()).isEqualTo(1);
        assertThat(panier.detail(client.getId()).lignes()).isEmpty();
        mission.setPrix(new BigDecimal("90.00"));
        assertThat(commandeService.detail(id, client.getId()).total()).isEqualByComparingTo("29.00");
        mvc.perform(get("/commandes").with(user(client))).andExpect(status().isOk());
        mvc.perform(get("/commandes/" + id).with(user(client))).andExpect(status().isOk());
    }

    @Test
    void refuseUneDoubleConfirmation() {
        ajouter("Younes");
        commandeService.confirmer(client.getId());
        assertThatThrownBy(() -> commandeService.confirmer(client.getId())).isInstanceOf(IllegalArgumentException.class);
        assertThat(commandes.count()).isEqualTo(1);
    }

    @Test
    void refuseUnPanierQuiDepasseLesPlaces() {
        mission.setPlacesDisponibles(1);
        ajouter("Younes");
        ajouter("MichaelJackson");
        assertThatThrownBy(() -> commandeService.confirmer(client.getId())).isInstanceOf(IllegalArgumentException.class);
        assertThat(commandes.count()).isZero();
        assertThat(panier.detail(client.getId()).lignes()).hasSize(2);
        assertThat(mission.getPlacesDisponibles()).isEqualTo(1);
    }

    @Test
    void controleLesRolesLaProprieteEtLeCsrf() throws Exception {
        mvc.perform(get("/panier")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/admin/fusees/ajouter").with(user(client))).andExpect(status().isForbidden());
        mvc.perform(post("/commandes").with(user(client))).andExpect(status().isForbidden());
        ajouter("Younes");
        Long ligne = panier.detail(client.getId()).lignes().get(0).id();
        assertThatThrownBy(() -> panier.retirer(autre.getId(), ligne)).isInstanceOf(AccessDeniedException.class);
        Long id = commandeService.confirmer(client.getId());
        mvc.perform(get("/commandes/" + id).with(user(autre))).andExpect(status().isForbidden());
    }

    @Test
    void valideLesFormulairesEtIgnoreLeRoleEnvoye() throws Exception {
        mvc.perform(post("/inscription").with(csrf()).param("username", "nouveau")
                .param("email", "nouveau@example.com").param("password", "Nouveau123!").param("role", "ADMIN"))
                .andExpect(status().is3xxRedirection());
        assertThat(utilisateurs.findByUsername("nouveau").orElseThrow().getRole()).isEqualTo(Role.CLIENT);
        mvc.perform(post("/panier/ajouter/" + mission.getId()).with(user(client)).with(csrf()).param("nomInscrit", " "))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("form", "nomInscrit"));
        mvc.perform(post("/admin/fusees/ajouter").with(user(admin)).with(csrf()).param("nom", ""))
                .andExpect(status().isOk()).andExpect(model().attributeHasErrors("form"));
        mvc.perform(post("/admin/missions/ajouter").with(user(admin)).with(csrf()).param("nom", ""))
                .andExpect(status().isOk()).andExpect(model().attributeHasErrors("form"));
    }

    @Test
    void achatParLeControleur() throws Exception {
        mvc.perform(post("/panier/ajouter/" + mission.getId()).with(user(client)).with(csrf()).param("nomInscrit", "Younes"))
                .andExpect(redirectedUrl("/panier"));
        mvc.perform(post("/commandes").with(user(client)).with(csrf())).andExpect(status().is3xxRedirection());
        assertThat(commandes.count()).isEqualTo(1);
        mvc.perform(post("/commandes").with(user(client)).with(csrf())).andExpect(redirectedUrl("/panier"));
    }

    @Test
    void depotReserveALastronauteDeLaMission() throws Exception {
        ajouter("Younes");
        Long commandeId = commandeService.confirmer(client.getId());
        Long billetId = commandeService.detail(commandeId, client.getId()).lignes().get(0).id();
        mvc.perform(get("/astronaute").with(user(nora))).andExpect(status().isOk());
        mvc.perform(get("/astronaute/missions/" + mission.getId()).with(user(nora))).andExpect(status().isOk());
        assertThatThrownBy(() -> commandeService.deposer(mission.getId(), billetId, nora.getId()))
                .isInstanceOf(IllegalArgumentException.class);
        missionService.avancer(mission.getId(), nora.getId());
        assertThatThrownBy(() -> missionService.avancer(mission.getId(), nora.getId()))
                .isInstanceOf(IllegalArgumentException.class);
        Utilisateur autreAstronaute = compte("sam", Role.ASTRONAUTE);
        assertThatThrownBy(() -> commandeService.deposer(mission.getId(), billetId, autreAstronaute.getId()))
                .isInstanceOf(AccessDeniedException.class);
        commandeService.deposer(mission.getId(), billetId, nora.getId());
        missionService.avancer(mission.getId(), nora.getId());
        assertThat(mission.getStatut()).isEqualTo(StatutMission.TERMINEE);
        assertThat(billets.findById(billetId).orElseThrow().getStatut()).isEqualTo(StatutInscription.DEPOSEE);
    }

    @Test
    void adminPeutCreerModifierEtSupprimerUneFusee() throws Exception {
        mvc.perform(post("/admin/fusees/ajouter").with(user(admin)).with(csrf())
                .param("nom", "Aster").param("description", "Lanceur fctif").param("imageUrl", ""))
                .andExpect(redirectedUrl("/fusees"));
        Fusee f = fusees.findAll().stream().filter(x -> x.getNom().equals("Aster")).findFirst().orElseThrow();
        mvc.perform(post("/admin/fusees/" + f.getId() + "/modifier").with(user(admin)).with(csrf())
                .param("nom", "Aster II").param("description", "Lanceur fictif").param("imageUrl", ""))
                .andExpect(redirectedUrl("/fusees"));
        assertThat(fusees.findById(f.getId()).orElseThrow().getNom()).isEqualTo("Aster II");
        mvc.perform(post("/admin/fusees/" + f.getId() + "/supprimer").with(user(admin)).with(csrf()))
                .andExpect(redirectedUrl("/fusees"));
        assertThat(fusees.existsById(f.getId())).isFalse();
    }

    private void ajouter(String nom) {
        AjoutPanierForm form = new AjoutPanierForm();
        form.setNomInscrit(nom);
        panier.ajouter(client.getId(), mission.getId(), form);
    }

    private Utilisateur compte(String nom, Role role) {
        Utilisateur u = new Utilisateur();
        u.setUsername(nom);
        u.setEmail(nom + "@michaelJackson.com");
        u.setPassword("test");
        u.setRole(role);
        return utilisateurs.save(u);
    }
}
