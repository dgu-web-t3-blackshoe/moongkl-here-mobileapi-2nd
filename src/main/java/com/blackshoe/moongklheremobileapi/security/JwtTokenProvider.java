package com.blackshoe.moongklheremobileapi.security;

import com.blackshoe.moongklheremobileapi.dto.JwtDto;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Component @Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private Long jwtTokenExpiry;

    public String createAccessToken(JwtDto.JwtRequestDto jwtRequestDto) {
        Date now = new Date();
        //LocalDateTime 쓰면 불변성때문에 연산 못함
        Date tokenExpiryDate = new Date(now.getTime() + jwtTokenExpiry);

        log.info("jwtAccessToken 생성");

        String token = Jwts.builder()
                .setSubject(jwtRequestDto.getEmail())
                .claim("userId", jwtRequestDto.getUserId().toString())
                .setIssuedAt(now)
                .setExpiration(tokenExpiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .setIssuer("moongklhere")
                .compact();
        log.info("jwtAccessToken 생성 완료 : " + token);

        return token;
    }

//    public String createAccessToken(String username, String userType) {
//        Date now = new Date();
//        Date tokenExpiryDate = new Date(now.getTime() + jwtTokenExpiry);
//
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(now)
//                .claim("userType", userType)
//                .setExpiration(tokenExpiryDate)
//                .signWith(SignatureAlgorithm.HS512, jwtSecret)
//                .compact();
//    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return UUID.fromString(claims.get("userId", String.class));
    }
    public boolean validateUserIdWithTokenClaims(String token, UUID userId) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

        String userIdInToken = claims.get("userId", String.class);

        if (userIdInToken.equals(userId.toString()))
            return true;

        log.info("요청된 사용자 ID가 토큰과 일치하지 않습니다");
        return false;
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            System.out.println("유효하지 않은 JWT 서명입니다");
        } catch (MalformedJwtException ex) {
            System.out.println("유효하지 않은 JWT 토큰입니다");
        } catch (ExpiredJwtException ex) {
            System.out.println("만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException ex) {
            System.out.println("지원하지 않는 JWT 토큰입니다");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT 토큰이 비어있습니다");
        }
        return false;
    }

}
