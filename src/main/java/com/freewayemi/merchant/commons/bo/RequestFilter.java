package com.freewayemi.merchant.commons.bo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class RequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String sessionId = ((HttpServletRequest) request).getSession() == null ? ""
                    : ((HttpServletRequest) request).getSession().getId();
            MDC.put("sessionId", sessionId);
            long start = System.currentTimeMillis();
            filterChain.doFilter(sanitise(request), response);
            long time = System.currentTimeMillis() - start;
            LOGGER.info("Total request processing time on URI: {} is: {}", request.getRequestURI(), time);
        } finally {
            MDC.remove("sessionId");
        }
        SecurityContextHolder.clearContext();
    }

    private ServletRequest sanitise(HttpServletRequest request) {
        try {
            if (request.getContentType() != null &&
                    request.getContentType().toLowerCase().contains("multipart/form-data")) {
                LOGGER.info("It is Multipart request. Not performing xss sanitization. ");
                return request;
            } else {
                return request;
//                return new XSSRequestWrapper(request);
            }
        } catch (Exception ex) {
            return request;
        }
    }
}
