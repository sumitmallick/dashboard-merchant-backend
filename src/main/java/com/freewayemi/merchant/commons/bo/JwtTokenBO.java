package com.freewayemi.merchant.commons.bo;

import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.TokenCreationRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtTokenBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenBO.class);

    private static final long JWT_TOKEN_VALIDITY = 7 * 24 * 60 * 60;
    private static final long JWT_TEMP_TOKEN_VALIDITY = 30 * 60;
    private final String secret;

    @Autowired
    public JwtTokenBO(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    public String generateTempToken(String id, List<String> authorities) {
        return Jwts.builder()
                .claim("authorities", authorities)
                .setSubject(id)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TEMP_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String generateToken(String mobile, String deviceToken, List<String> authorities) {
        return generateToken(mobile, deviceToken, authorities, "");
    }

    public synchronized String generateToken(String mobile, String deviceToken, List<String> authorities,
                                             String storeUserId) {
        LOGGER.info("Creating token for id: {} with authorities: {}", mobile, authorities);
        return Jwts.builder()
                .claim("authorities", authorities)
                .claim("deviceToken", Util.md5(deviceToken))
                .claim("storeUserId", storeUserId)
                .setSubject(mobile)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public synchronized String generateToken(String mobile, String deviceToken, TokenCreationRequest tokenCreationRequest) {
        return Jwts.builder()
                .claim("id",tokenCreationRequest.getId())
                .claim("name",tokenCreationRequest.getName())
                .claim("user",tokenCreationRequest.getUser())
                .claim("exp",tokenCreationRequest.getExp())
                .claim("type",tokenCreationRequest.getType())
                .claim("role",tokenCreationRequest.getRole())
                .claim("userType",tokenCreationRequest.getUserType())
                .claim("permissions",tokenCreationRequest.getPermissions())
                .claim("merchantId",tokenCreationRequest.getMerchantId())
                .claim("merchantID",tokenCreationRequest.getMerchantId())
                .claim("merchantIds",tokenCreationRequest.getMerchantIds())
                .claim("shopName",tokenCreationRequest.getShopName())
                .claim("session",tokenCreationRequest.getSession())
                .claim("deviceToken", Util.md5(deviceToken))
                .setSubject(mobile)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public boolean validateToken(String token, /*UserDetails userDetails*/String user) {
        final String username = getUsernameFromToken(token);
        return (username.equals(user) && !isTokenExpired(token));
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    @SuppressWarnings("unchecked")
    public List<String> getAuthoritiesFromToken(String jwtToken) {
        final Claims claims = getAllClaimsFromToken(jwtToken);
        return claims.get("authorities", List.class);
    }

    public String getStoreUserId(String jwtToken) {
        final Claims claims = getAllClaimsFromToken(jwtToken);
        return claims.get("storeUserId", String.class);
    }
}
