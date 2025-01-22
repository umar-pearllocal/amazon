package com.documentor.amazon.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableWebSecurity
@EnableTransactionManagement
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self';" +
                                        "script-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com;" +
                                        "style-src 'self' 'unsafe-inline';" +
                                        "connect-src 'self' http://localhost:5555 wss://api.pilling.in file://*;")
                        )
                )
                .authorizeHttpRequests(authorizationRequests -> authorizationRequests
                        // Add /verifyUser to accessible endpoints with appropriate authority
                        .requestMatchers("/api/v0/**","/api/v0").permitAll()
                        .anyRequest().authenticated()
                )

                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}


