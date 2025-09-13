package com.anujl.collaborative_code_editor.config;




import com.anujl.collaborative_code_editor.entity.UserEntity;
import com.anujl.collaborative_code_editor.repository.UserRepo;
import com.anujl.collaborative_code_editor.service.CustomUserDetailService;
import com.anujl.collaborative_code_editor.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        Date issuedAt=null;

        String requestPath = request.getServletPath();


        if (requestPath.startsWith("/api/auth/register") || requestPath.startsWith("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

                token = token.trim();

            System.out.println(token);
            username = jwtService.extractUserName(token);
            issuedAt = jwtService.extractIssuedAt(token);
        }

        if (issuedAt!=null && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserEntity userEntity = userRepo.findByUsername(username)
                    .orElse(null);
            UserDetails userDetails = context.getBean(CustomUserDetailService.class).loadUserByUsername(username);

            if (userEntity != null && userEntity.getLastLoginTime() != null) {
                if (issuedAt.toInstant().isBefore( userEntity.getLastLoginTime().atZone(ZoneId.systemDefault()).toInstant())) {
                    System.out.println("1Token issued at: " + issuedAt + " is before last login time: " + userEntity.getLastLoginTime());

                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired by logout");
                    return;
                }

                System.out.println("Token issued at: " + issuedAt + " is before last login time: " + userEntity.getLastLoginTime());
                System.out.println("Token issued at: " + issuedAt);
            }
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

}
