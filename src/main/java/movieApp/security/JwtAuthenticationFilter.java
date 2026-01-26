package movieApp.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;
    private final CustomUserDetailService customUserDetailService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailService customUserDetailService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailService = customUserDetailService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Optional<String> token = getTokenFromRequest(request);

        if (token.isPresent() && jwtUtils.validateToken(token.get())) {
            String username = jwtUtils.getUsernameFromToken(token.get());
            log.info("Token valid for user: {}", username);

            UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("User {} authenticated successfully", username);
        } else if (token.isPresent()) {
            log.warn("Invalid token received");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public Optional<String> getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.debug("Extracted JWT token (first 20 chars): {}...",
                    token.substring(0, Math.min(20, token.length())));
            return Optional.of(token);
        }

        log.debug("No Bearer token found in request");
        return Optional.empty();
    }
}