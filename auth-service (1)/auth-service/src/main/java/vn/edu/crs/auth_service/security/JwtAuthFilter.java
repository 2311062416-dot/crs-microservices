package vn.edu.crs.auth_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        System.out.println(">>> [FILTER] URI: " + request.getRequestURI() + " | AuthHeader: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                System.out.println(">>> [FILTER] Parsed Username: " + username + " | Raw Role: " + role);

                String formattedRole = (role != null && !role.isBlank())
                        ? (role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        : "ROLE_USER";

                System.out.println(">>> [FILTER] Final Authority: " + formattedRole);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, List.of(new SimpleGrantedAuthority(formattedRole))
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println(">>> [FILTER] Token INVALID!");
            }
        }
        filterChain.doFilter(request, response);
    }
}