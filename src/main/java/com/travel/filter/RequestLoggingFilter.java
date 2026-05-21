package com.travel.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();

        chain.doFilter(request, response);

        long duration = System.currentTimeMillis() - start;

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        int status = response.getStatus();

        if (query != null) {
            uri += "?" + query;
        }

        log.info("[REQUEST] {} {} | IP: {} | Status: {} | {}ms | UA: {}",
                method, uri, ip, status, duration, ua != null ? ua.substring(0, Math.min(ua.length(), 50)) : "N/A");
    }
}
