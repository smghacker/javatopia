package com.vmware.sofia.games.javatopia.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@EnableAutoConfiguration
@ComponentScan
@Controller
public class MethodSecurityConfig {
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String name = authentication.getName();
                String password = authentication.getCredentials().toString();
                String p = System.getProperty("password");
                if (p== null) {
                    p = "password";
                }
                if (!p.equals(password)) throw new BadCredentialsException("Invalid password");

                List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();

                grantedAuths.add(new GrantedAuthorityImpl("ROLE_ADMIN"));

                return new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
            }

            @Override
            public boolean supports(Class<?> aClass) {

                return aClass.equals(UsernamePasswordAuthenticationToken.class);
            }
        });
    }
}