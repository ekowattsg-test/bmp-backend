package com.hcteol.jwt.backend.config;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcteol.jwt.backend.dtos.ErrorDto;
import com.hcteol.jwt.backend.dtos.UserDto;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final UserAuthenticationProvider userAuthenticationProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {
        String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null) {
            String[] authElements = header.split(" ");

            if (authElements.length == 2
                    && "Bearer".equals(authElements[0])) {
                try {
                    Authentication authentication = userAuthenticationProvider.validateToken(authElements[1]);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    maybeAttachRefreshedToken(httpServletResponse, authentication);
                } catch (TokenExpiredException e) {
                    SecurityContextHolder.clearContext();
                    writeUnauthorized(httpServletResponse, "Session Expired");
                    return;
                } catch (JWTVerificationException e) {
                    SecurityContextHolder.clearContext();
                    writeUnauthorized(httpServletResponse, "Invalid token");
                    return;
                } catch (RuntimeException e) {
                    SecurityContextHolder.clearContext();
                    throw e;
                }
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void maybeAttachRefreshedToken(HttpServletResponse response, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDto user)) {
            return;
        }

        String refreshedToken = userAuthenticationProvider.refreshToken(user);
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + refreshedToken);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        OBJECT_MAPPER.writeValue(response.getOutputStream(), new ErrorDto(message));
    }
}
