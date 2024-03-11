package org.anyone.backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;

public final class JwtUtil {
//    private final static Password key = Keys.password("@shabidss".toCharArray());
    private final static SecretKey key = Jwts.SIG.HS512.key().build(); //or HS384.key() or HS512.key()
    private final static Duration expiration = Duration.ofDays(30);
    private final static Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public static String generate(String username) {
        Date expiryDate = new Date(System.currentTimeMillis() + expiration.toMillis());
        return Jwts.builder()
                .subject(username)
                .expiration(expiryDate)
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public static Claims parse(String compact) {
        if (compact.isEmpty()) {
            return null;
        }
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(compact);
            return jws.getPayload();
        } catch (JwtException exception) {
            logger.error("JWT invalid");
            logger.error(exception.toString());
            return null;
        }
    }
}
