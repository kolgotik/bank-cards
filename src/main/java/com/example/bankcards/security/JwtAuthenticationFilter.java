package com.example.bankcards.security;

import com.example.bankcards.service.BankUserService;
import com.example.bankcards.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtUtil jwtUtil;
    private final BankUserService bankUserService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Получаем токен из заголовка
            var authHeader = request.getHeader(HEADER_NAME);
            if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Обрезаем префикс и получаем имя пользователя из токена
            var jwt = authHeader.substring(BEARER_PREFIX.length());
            var username = jwtUtil.extractUserName(jwt);

            if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = bankUserService
                        .userDetailsService()
                        .loadUserByUsername(username);

                // Если токен валиден, то аутентифицируем пользователя
                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            sendErrorResponse(response, "Invalid or expired token", HttpStatus.UNAUTHORIZED.value());
            return;
        } catch (UsernameNotFoundException e) {
            log.error("User not found for JWT token: {}", e.getMessage());
            sendErrorResponse(response, "User not found", HttpStatus.FORBIDDEN.value());
            return;

        } catch (Exception e) {
            log.error("Unexpected error in JwtAuthenticationFilter: {}", e.getMessage(), e);
            sendErrorResponse(response, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(message);
    }
}
