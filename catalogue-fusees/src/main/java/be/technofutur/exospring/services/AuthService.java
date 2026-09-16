package be.technofutur.exospring.services;

import be.technofutur.exospring.entities.Utilisateur;
import be.technofutur.exospring.enums.Role;
import be.technofutur.exospring.models.auth.*;
import be.technofutur.exospring.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final UtilisateurRepository utilisateurs;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return utilisateurs.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Compte introuvable"));
    }

    @Transactional
    public void inscrire(InscriptionForm form) {
        String email = form.getEmail().strip().toLowerCase(Locale.ROOT);
        if (utilisateurs.existsByUsername(form.getUsername()) || utilisateurs.existsByEmail(email)) {
            throw new IllegalArgumentException("Pseudo ou email déjà utilisé");
        }
        Utilisateur u = new Utilisateur();
        u.setUsername(form.getUsername());
        u.setEmail(email);
        u.setPassword(encoder.encode(form.getPassword()));
        u.setRole(Role.CLIENT);
        utilisateurs.save(u);
    }

    @Transactional(readOnly = true)
    public List<UtilisateurDto> astronautes() {
        return utilisateurs.findByRoleOrderByUsername(Role.ASTRONAUTE).stream()
                .map(UtilisateurDto::fromEntity).toList();
    }
}
