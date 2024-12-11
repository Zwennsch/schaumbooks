package de.schaumburg.schaumbooks.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(customizer -> customizer.disable());
        // this means every request for every url has to be authenticated
        http.authorizeHttpRequests(request -> request.anyRequest().permitAll());

        // http.csrf(customizer -> customizer.disable())
        // .authorizeHttpRequests(registry -> {
        // registry.requestMatchers("/api/books").permitAll();
        // });

        return http.build();
    }
}
