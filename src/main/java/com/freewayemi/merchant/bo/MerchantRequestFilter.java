package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.bo.JwtTokenBO;
import com.freewayemi.merchant.entity.MerchantSession;
import com.freewayemi.merchant.repository.MerchantSessionsRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Order(2)
public class MerchantRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantRequestFilter.class);

    private final JwtTokenBO jwtTokenBO;
    private final MerchantSessionsRepository merchantSessionsRepository;

    @Autowired
    public MerchantRequestFilter(JwtTokenBO jwtTokenBO, MerchantSessionsRepository merchantSessionsRepository) {
        this.jwtTokenBO = jwtTokenBO;
        this.merchantSessionsRepository = merchantSessionsRepository;
    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        List<String> skipFilterUrls = new ArrayList<>();
        skipFilterUrls.add("/internal/api/v1/login");
        skipFilterUrls.add("/internal/redis/clearCache");
        skipFilterUrls.add("/internal/api/v1/brands/info");
        skipFilterUrls.add("/actuator/health");
        return skipFilterUrls.stream().anyMatch(url -> new AntPathRequestMatcher(url).matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String jwtToken;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                List<String> authorities = jwtTokenBO.getAuthoritiesFromToken(jwtToken);
                if (!authorities.contains("CONSUMER")) {

                    Optional<MerchantSession> merchantSession =
                            merchantSessionsRepository.findByToken(Util.md5(jwtToken));
                    if (merchantSession.isPresent()) {
                        MerchantSession session = merchantSession.get();
                        if (session.getInvalid()) {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please pass valid jwt token.");
                            return;
                        } else if (null != session.getLastActivityDate() &&
                                session.getLastActivityDate().plus(1, ChronoUnit.HOURS).isBefore(Instant.now())) {
                            session.setLastActivityDate(Instant.now());
                            merchantSessionsRepository.save(session);
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please pass valid jwt token.");
                        return;
                    }
                }
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                LOGGER.warn("JWT Token has expired");
            }
        } else {
            LOGGER.warn("JWT Token does not begin with Bearer String");
        }
        filterChain.doFilter(request, response);
    }
}
