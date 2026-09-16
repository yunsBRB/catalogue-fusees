package be.technofutur.exospring;

import be.technofutur.exospring.entities.*;
import be.technofutur.exospring.enums.*;
import be.technofutur.exospring.models.panier.AjoutPanierForm;
import be.technofutur.exospring.repositories.*;
import be.technofutur.exospring.services.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:transactions;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000",
        "spring.jpa.hibernate.ddl-auto=create-drop", "app.demo=false"
})
class TransactionsTest {
    @Autowired UtilisateurRepository utilisateurs;
    @Autowired FuseeRepository fusees;
    @Autowired MissionRepository missions;
    @Autowired CommandeRepository commandes;
    @Autowired PanierService panier;
    @Autowired CommandeService achats;
    @Autowired TransactionTemplate transaction;

    @Test
    void uneSeuleDernierePlacePourDeuxClients() throws Exception {
        Long[] ids = preparer();
        ajouter(ids[0], ids[2]);
        ajouter(ids[1], ids[2]);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch depart = new CountDownLatch(1);
        try {
            Future<Boolean> a = executor.submit(() -> acheter(ids[0], depart));
            Future<Boolean> b = executor.submit(() -> acheter(ids[1], depart));
            depart.countDown();
            assertThat(a.get(15, TimeUnit.SECONDS) ^ b.get(15, TimeUnit.SECONDS)).isTrue();
            assertThat(missions.findById(ids[2]).orElseThrow().getPlacesDisponibles()).isZero();
            assertThat(commandes.findByUtilisateurIdOrderByIdDesc(ids[0]).size()
                    + commandes.findByUtilisateurIdOrderByIdDesc(ids[1]).size()).isEqualTo(1);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void achatIncompletAnnuleToutesLesModifications() {
        Long[] ids = preparer();
        ajouter(ids[0], ids[2]);
        ajouter(ids[0], ids[3]);
        transaction.executeWithoutResult(s -> missions.findById(ids[3]).orElseThrow().setPlacesDisponibles(0));
        assertThatThrownBy(() -> achats.confirmer(ids[0])).isInstanceOf(IllegalArgumentException.class);
        assertThat(missions.findById(ids[2]).orElseThrow().getPlacesDisponibles()).isEqualTo(1);
        assertThat(panier.detail(ids[0]).lignes()).hasSize(2);
        assertThat(commandes.findByUtilisateurIdOrderByIdDesc(ids[0])).isEmpty();
    }

    private boolean acheter(Long utilisateur, CountDownLatch depart) throws InterruptedException {
        depart.await();
        try {
            achats.confirmer(utilisateur);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void ajouter(Long utilisateur, Long mission) {
        AjoutPanierForm form = new AjoutPanierForm();
        form.setNomInscrit("Younes");
        panier.ajouter(utilisateur, mission, form);
    }

    private Long[] preparer() {
        return transaction.execute(s -> {
            Utilisateur a = compte(Role.CLIENT);
            Utilisateur b = compte(Role.CLIENT);
            Utilisateur nora = compte(Role.ASTRONAUTE);
            Fusee f = new Fusee();
            f.setNom("Saturn");
            f.setDescription("Lanceur");
            fusees.save(f);
            Mission m1 = mission(f, nora);
            Mission m2 = mission(f, nora);
            return new Long[]{a.getId(), b.getId(), m1.getId(), m2.getId()};
        });
    }

    private Utilisateur compte(Role role) {
        Utilisateur u = new Utilisateur();
        u.setUsername(UUID.randomUUID().toString().substring(0, 20));
        u.setEmail(u.getUsername() + "@example.com");
        u.setPassword("test");
        u.setRole(role);
        return utilisateurs.save(u);
    }

    private Mission mission(Fusee f, Utilisateur u) {
        Mission m = new Mission();
        m.setNom("Séléné");
        m.setFusee(f);
        m.setAstronaute(u);
        m.setDateDepart(LocalDate.now().plusDays(30));
        m.setPrix(new BigDecimal("29.00"));
        m.setPlacesDisponibles(1);
        m.setStatut(StatutMission.PLANIFIEE);
        return missions.save(m);
    }
}
