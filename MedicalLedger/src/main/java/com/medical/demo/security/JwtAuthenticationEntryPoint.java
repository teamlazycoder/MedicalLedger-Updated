package com.medical.demo.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*; import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType; import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint; import org.springframework.stereotype.Component;
import java.io.IOException; import java.util.*;

@Slf4j @Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String,Object> body = new HashMap<>();
        body.put("status",401); body.put("error","Unauthorized"); body.put("message",e.getMessage()); body.put("path",request.getServletPath());
        new ObjectMapper().writeValue(response.getOutputStream(),body);
    }
}