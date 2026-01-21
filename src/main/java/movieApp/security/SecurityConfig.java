package movieApp.security;

import movieApp.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Добавьте этот импорт
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;

    public SecurityConfig(CustomUserDetailService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(HttpMethod.POST, "/security/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/films").permitAll()
                                .requestMatchers(HttpMethod.GET,"/films/{id}").permitAll()
                                .requestMatchers(HttpMethod.POST, "/security/jwt").permitAll()
                                .requestMatchers(HttpMethod.GET, "/user").permitAll()
                                .requestMatchers(HttpMethod.GET, "/user/myself").permitAll()
                                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .userDetailsService(customUserDetailService)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}