package org.api.workaround.configuration;

import org.api.workaround.model.enums.HttpRequestMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final static String HTML_INJECTION_POLICY = "script-src 'self'";

    private final List<String> allowedOrigins = List.of("http://localhost:8081");
    private final List<String> methods = List.of(HttpRequestMethod.GET.toString(), HttpRequestMethod.POST.toString());
    private final List<String> headers = List.of("Content-type", "Authorization");

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(crs -> crs.configurationSource(corsConfiguration()))
                .authorizeHttpRequests(req -> req.anyRequest().permitAll())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.xssProtection(x -> x.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)).contentSecurityPolicy(cps -> cps.policyDirectives(HTML_INJECTION_POLICY)))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(methods);
        configuration.setAllowedHeaders(headers);
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(allowedOrigins);
        UrlBasedCorsConfigurationSource urlBased = new UrlBasedCorsConfigurationSource();
        urlBased.registerCorsConfiguration("/**", configuration);
        return urlBased;
    }

    @Bean
    AuthenticationManager authManager() {
        return authentication -> {
            throw new IllegalArgumentException("Authentication is disabled!");
        };
    }
}
