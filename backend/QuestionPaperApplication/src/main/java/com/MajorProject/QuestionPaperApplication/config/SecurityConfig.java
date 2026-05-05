package com.MajorProject.QuestionPaperApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // 🔓 Public APIs
                        .requestMatchers("/auth/**").permitAll()

                        // ✅ USER + ADMIN (READ + GENERATE + PDF)
                        .requestMatchers(
                                "/questions",
                                "/questions/smart",
                                "/questions/export",
                                "/questions/search",
                                "/questions/page",
                                "/questions/generate-flexible",
                                "/questions/generate-by-marks",
                                "/questions/generate-selected",
                                "/questions/export-selected"
                        ).hasAnyRole("USER", "ADMIN")

                        // 🔒 ADMIN ONLY (ADD / UPDATE / DELETE)
                        .requestMatchers("/questions/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}