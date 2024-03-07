package org.anyone.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.anyone.backend.repository.UserRepository;
import org.anyone.backend.service.UserDetailsService;
import org.anyone.backend.util.JwtUtil;
import org.anyone.backend.util.ResponseData;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    public JwtRequestFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private void returnErrorCode(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(
                new ResponseData<>(401, "token invalid", null)
        ));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request
            , @Nonnull HttpServletResponse response
            , @Nonnull FilterChain filterChain) throws ServletException, IOException {
        // header is fucked
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            returnErrorCode(response);
//            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7);
        Claims claims = JwtUtil.parse(token);
        if (claims == null) {
            returnErrorCode(response);
//            filterChain.doFilter(request, response);
            return;
        }

        final String username = claims.getSubject();
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException e) {
            returnErrorCode(response);
//            filterChain.doFilter(request, response);
        }
    }
}
