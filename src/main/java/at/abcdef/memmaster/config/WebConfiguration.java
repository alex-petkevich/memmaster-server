package at.abcdef.memmaster.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.UrlHandlerFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

    private final ApplicationProperties applicationProperties;

    public WebConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public UrlHandlerFilter trailingSlashHandlerFilter() {
        // Preserve legacy behavior where both /path and /path/ are accepted.
        return UrlHandlerFilter.trailingSlashHandler("/**").wrapRequest().build();
    }

    /**
     * Exposed as a bean so Spring Security 7's http.cors(Customizer.withDefaults())
     * picks it up automatically and handles CORS inside the security filter chain
     * (before authentication), ensuring CORS headers are present even on 401/403 responses.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(parseAllowedOrigins(applicationProperties.getCors().getAllowedOrigins())));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setExposedHeaders(List.of(applicationProperties.getCors().getExposedHeaders().split(",")));
        config.setAllowCredentials(Boolean.parseBoolean(applicationProperties.getCors().getAllowCredentials()));
        config.setMaxAge(Long.parseLong(applicationProperties.getCors().getMaxAge()));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    private String[] parseAllowedOrigins(String allowedOrigins) {
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .map(origin -> origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin)
                .toArray(String[]::new);
    }
}
