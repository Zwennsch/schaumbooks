package de.schaumburg.schaumbooks.user;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails{
    private final User user;

    public CustomUserDetails(User user){
        this.user = user;
    }
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(Collectors.toList());
    }

    // TODO: I am not sure if I would need those. Remove if proven unneeded.

    // @Override
    // public boolean isAccountNonExpired() { return true; }

    // @Override
    // public boolean isAccountNonLocked() { return true; }

    // @Override
    // public boolean isCredentialsNonExpired() { return true; }

    // @Override
    // public boolean isEnabled() { return true; }

    
    
}
