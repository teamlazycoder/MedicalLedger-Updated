package com.medical.demo.security;
import com.medical.demo.service.auth.CustomUserDetailsService;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull; import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component; import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j @Component @RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        if(path.startsWith("/api/v1/auth/")||path.startsWith("/api/health")||path.startsWith("/test")||
                path.startsWith("/swagger")||path.startsWith("/v3/api-docs")||path.startsWith("/h2-console")||
                path.startsWith("/error")||path.startsWith("/api/v1/doctors/search")) {
            filterChain.doFilter(request,response); return;
        }
        String jwt = getJwtFromRequest(request);
        if(StringUtils.hasText(jwt)) {
            try {
                if(jwtTokenProvider.validateToken(jwt)) {
                    String email = jwtTokenProvider.getEmailFromToken(jwt);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    var auth = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch(Exception ex) { log.error("Auth error: {}",ex.getMessage()); SecurityContextHolder.clearContext(); }
        }
        filterChain.doFilter(request,response);
    }
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (StringUtils.hasText(bearer)&&bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }
}