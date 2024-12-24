package com.example.defiExchange.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login", "/oauth2/**", "/logout", "/logout-success").permitAll()  // Allow login and OAuth callback
                        .anyRequest().authenticated()  // Protect other URLs
                )
                .formLogin(form -> form
                        .loginPage("/login")  // Custom login page
                        .defaultSuccessUrl("/dashboard", true)  // Redirect to dashboard after successful login
                        .permitAll()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .loginPage("/login")  // Custom login page
                                .defaultSuccessUrl("/dashboard", true)  // Redirect to dashboard after successful login
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/logout-success")  // Redirect to logout success page
                        .permitAll()
                );

        return http.build();
    }
}
