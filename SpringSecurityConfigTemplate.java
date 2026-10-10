package com.yourapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfigTemplate {

    private final JwtAuthFilter jwtAuthFilter;

    public SpringSecurityConfigTemplate(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http

                // =======================================================
                // DECISION 1: WHAT TO PROTECT?
                // Decide what attacks we care about.
                // CSRF -> Disable for stateless APIs (no cookies)
                // CORS -> Define who can call your API from browser
                // =======================================================
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // =======================================================
                // DECISION 2: SESSION TYPE?
                // STATELESS = No server session. Every request has JWT. Best for APIs.
                // STATEFUL = Server remembers you. Uses JSESSIONID. Best for MVC apps.
                // =======================================================
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // =======================================================
                // DECISION 3: PUBLIC vs PRIVATE?
                // This is where 90% of bugs happen. Order matters!
                // Specific rules first, generic last.
                // =======================================================
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**", "/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated() // everything else needs login
                )

                // =======================================================
                // DECISION 4: CUSTOM FILTERS?
                // Where your custom logic lives.
                // JWT filter checks token BEFORE default login filter.
                // If you have API key, RateLimit, etc. add them here.
                // =======================================================
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // =======================================================
                // DECISION 5: HOW TO LOGIN?
                // How user proves identity. Pick one.
                // httpBasic -> for quick testing / Postman
                // formLogin -> for browser apps
                // oauth2Login -> for Google/GitHub login
                // =======================================================
                .httpBasic(Customizer.withDefaults())
                // .formLogin(Customizer.withDefaults())

                .build();
    }

    // CORS config - who is allowed to call you
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://yourapp.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
