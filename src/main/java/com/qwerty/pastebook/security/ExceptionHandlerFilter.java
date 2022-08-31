package com.qwerty.pastebook.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qwerty.pastebook.exceptions.BadRequestException;
import com.qwerty.pastebook.exceptions.ForbiddenException;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            Map<String, String> message = new HashMap<>();
            response.setContentType("application/json");

            message.put("message", e.getMessage());
            response.getWriter().write(convertObjectToJson(message));
            if (e.getClass() == ForbiddenException.class) {
                response.setStatus(403);
                return;
            }

            response.setStatus(500);
            log.error("Unhandled exception in exception handler filter. Class: " + e.getClass() + ". Message: " + e.getMessage());
        }
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
