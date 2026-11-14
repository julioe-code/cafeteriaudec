package co.edu.ucundinamarca.cafeteria.menu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * CF-SEGU-0001: solo el encargado autenticado puede modificar el menú.
 * - Consultar el menú es público (solo lectura).
 * - /api/admin/** exige usuario y contraseña con rol ENCARGADO.
 * - Todo lo que no esté permitido explícitamente se niega (denegar por defecto).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filtroSeguridad(HttpSecurity http) throws Exception {
        JsonAuthenticationEntryPoint noAutenticado = new JsonAuthenticationEntryPoint();
        http
                // API sin sesión ni cookies (la credencial viaja en la cabecera Authorization),
                // por eso no aplica la protección CSRF basada en cookies.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/", "/index.html", "/admin.html",
                                "/assets/**", "/favicon.ico").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/menu", "/api/menu/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ENCARGADO")
                        .anyRequest().denyAll())
                .httpBasic(basic -> basic.authenticationEntryPoint(noAutenticado))
                .exceptionHandling(e -> e.authenticationEntryPoint(noAutenticado))
                .headers(h -> h
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; frame-ancestors 'none'; form-action 'self'; object-src 'none'"))
                        .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)));
        return http.build();
    }

    @Bean
    public PasswordEncoder codificadorContrasenas() {
        // BCrypt por defecto: la contraseña nunca se guarda en texto plano
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService usuarios(
            @Value("${app.seguridad.encargado.usuario}") String usuario,
            @Value("${app.seguridad.encargado.password}") String contrasena,
            PasswordEncoder codificador) {

        if (contrasena == null || contrasena.length() < 8) {
            throw new IllegalStateException("ENCARGADO_PASSWORD debe tener al menos 8 caracteres");
        }
        return new InMemoryUserDetailsManager(User.withUsername(usuario)
                .password(codificador.encode(contrasena))
                .roles("ENCARGADO")
                .build());
    }
}
