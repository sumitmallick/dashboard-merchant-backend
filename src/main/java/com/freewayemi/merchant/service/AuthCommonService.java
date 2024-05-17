package com.freewayemi.merchant.service;

import com.freewayemi.merchant.commons.bo.JwtTokenBO;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.dto.SecurityDetails;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthCommonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthCommonService.class);

    private final JwtTokenBO jwtTokenBO;

    @Autowired
    public AuthCommonService(JwtTokenBO jwtTokenBO) {
        this.jwtTokenBO = jwtTokenBO;
    }

    public SecurityDetails getMerchantId(HttpServletRequest request) {
        final String requestTokenHeader = request.getHeader("Authorization");
        String username;
        String jwtToken;
        String storeUserId = null;
        List<String> authorities;
        SecurityDetails securityDetails = new SecurityDetails();
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenBO.getUsernameFromToken(jwtToken);
                LOGGER.info("Request received using Bearer token for merchant: {}", username);
                authorities = jwtTokenBO.getAuthoritiesFromToken(jwtToken);
                storeUserId = jwtTokenBO.getStoreUserId(jwtToken);
                securityDetails.setMerchantIdOrDisplayId(username);
                Map<String, String> credentials = new HashMap<>();
                credentials.put("storeUserId", storeUserId);
                securityDetails.setCredentials(credentials);
                securityDetails.setAuthorities(authorities.stream()
                        .map(s -> "ROLE_" + s)
                        .collect(Collectors.toList()));
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                LOGGER.warn("JWT Token has expired");
            }
            return securityDetails;
        } else if (requestTokenHeader != null && requestTokenHeader.startsWith("Basic ")) {
            String token = requestTokenHeader.substring(6);
            try {
                String[] ss = new String(Base64.getDecoder().decode(token)).split(":");
                username = ss[0];
                authorities = Collections.singletonList("MERCHANT");
                securityDetails.setMerchantIdOrDisplayId(username);
                Map<String, String> credentials = new HashMap<>();
                credentials.put("storeUserId", storeUserId);
                securityDetails.setCredentials(credentials);
                securityDetails.setAuthorities(authorities.stream()
                        .map(s -> "ROLE_" + s)
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                LOGGER.error("basic credentials error.");
            }
            return securityDetails;
        } else {
            LOGGER.warn("JWT Token does not begin with Bearer String");
        }
        throw new FreewayException("Please pass valid token");
    }
}
