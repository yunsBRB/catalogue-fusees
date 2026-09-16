package be.technofutur.exospring.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(r -> r
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/astronaute/**").hasAuthority("ASTRONAUTE")
                .requestMatchers("/panier/**", "/commandes/**").hasAuthority("CLIENT")
                .anyRequest().permitAll())
                .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
                .logout(l -> l.logoutSuccessUrl("/"))
                .exceptionHandling(e -> e.accessDeniedPage("/acces-refuse"))
                .build();
    }
}
