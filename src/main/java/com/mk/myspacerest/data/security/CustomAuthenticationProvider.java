package com.mk.myspacerest.data.security;

import com.mk.myspacerest.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    private final String appPassword = "password";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        var username = authentication.getName();
        var password = authentication.getCredentials().toString();
        System.out.println(username + " " + password);

        if (basicAuthenticationIsValid(username, password)) { // Simplified for illustration
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("read"));
            return new UsernamePasswordAuthenticationToken("dvega4", "{noop}" + appPassword, authorities);
        } else {
            throw new AuthenticationException("Invalid username or password") {};
        }
    }

    private boolean basicAuthenticationIsValid(String username, String password) {
        boolean userNameIsValid = true;
        boolean passwordIsValid = password.equals(appPassword);
        return userNameIsValid && passwordIsValid;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
