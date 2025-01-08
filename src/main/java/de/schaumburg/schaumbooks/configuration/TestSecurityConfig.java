package de.schaumburg.schaumbooks.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // disable csrf since making the API stateless
        http.csrf(csrfConfigCustomizer -> csrfConfigCustomizer.disable());
        // this means every request for every endpoint is permitted
        // disable for now to implement security
        // http.authorizeHttpRequests(request -> request.anyRequest().permitAll());

        // ensures that every request requires authentication
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
        // disable form login since not using login page
            // .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
