package movieApp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Добавьте этот бин

    public SecurityConfig(CustomUserDetailService customUserDetailService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetailService = customUserDetailService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html",
                                        "/swagger-resources/**",
                                        "/webjars/**"
                                ).permitAll()
                                .requestMatchers(HttpMethod.POST, "/security/jwt").permitAll()
                                .requestMatchers(HttpMethod.POST, "/security/registration").permitAll()
                                .requestMatchers(HttpMethod.POST, "/security/role/{role}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/security/**").hasAnyRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/films").permitAll()
                                .requestMatchers(HttpMethod.GET, "/films/{id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/user").permitAll()
                                .requestMatchers(HttpMethod.GET, "/user/{username}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/user/myself").authenticated()
                                .requestMatchers(HttpMethod.GET,"/favorites/**").permitAll()
                                .requestMatchers("/favorites/**").authenticated()
                                .anyRequest().authenticated())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(customUserDetailService)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}