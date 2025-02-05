package de.schaumburg.schaumbooks.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // first you declare WHAT you want to protect and how:
                .authorizeHttpRequests(
                        authorizeHttp -> {
                            // ensures that every request requires authentication
                            authorizeHttp.anyRequest().authenticated();
                        })
                // secondly you declare HOW you want to login:
                // disable csrf since making the API stateless
                .csrf(csrfConfigCustomizer -> csrfConfigCustomizer.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();

    }


    // This is just for testing because in a real application those details should come from the database:
    // @Bean
    // public UserDetailsService userDetailsService() {
    //     UserDetails user1 = User.withDefaultPasswordEncoder()
    //             .username("sven")
    //             .password("1234")
    //             // .roles("USER")
    //             .build();
    //     UserDetails user2 = User.withDefaultPasswordEncoder()
    //             .username("hans")
    //             .password("3456")
    //             // .roles("USER")
    //             .build();
    //     return new InMemoryUserDetailsManager(user1, user2);
    // }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        return provider;
    }
}
