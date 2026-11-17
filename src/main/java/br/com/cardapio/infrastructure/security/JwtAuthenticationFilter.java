package br.com.cardapio.infrastructure.security;

import br.com.cardapio.adapters.out.security.JwtTokenProviderAdapter;
import br.com.cardapio.domain.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProviderAdapter tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProviderAdapter tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(tokenProvider.key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UserRole role = UserRole.valueOf(claims.get("role", String.class));
            String estabelecimentoIdRaw = claims.get("estabelecimentoId", String.class);
            UUID estabelecimentoId = estabelecimentoIdRaw == null ? null : UUID.fromString(estabelecimentoIdRaw);
            @SuppressWarnings("unchecked")
            List<String> permissions = (List<String>) claims.getOrDefault("permissions", List.of());
            AuthenticatedUser principal = new AuthenticatedUser(
                    UUID.fromString(claims.getSubject()),
                    claims.get("email", String.class),
                    role,
                    estabelecimentoId,
                    Set.copyOf(permissions)
            );

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
            permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority("PERM_" + p)));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, token, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException ignored) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
