package com.example.iclabs.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secret;

    private static final Key SECRET_KEYS = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /*
    untuk mengambil username menggunakan token
     */

    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public boolean isTokenValid(String token, UserDetails user){
        final String username = extractUsername(token);
        return (username.equals(user.getUsername()) || !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ini adalah cara tergampang untuk mengextract email menggunakan token
    public String extractUsernameByToken(String token){
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }



    /*
    untuk membuat sebuah token
     */

    public String generatedToken(UserDetails userDetails){
        return generatedToken(new HashMap<>(), userDetails);
    }





    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public String generatedToken(
            Map<String, Object> extractClaims,
            UserDetails userDetails
    ){

        Date currentDate = new Date();
        Date expirate = new Date(currentDate.getTime() + 70000);

        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(currentDate)
                .setExpiration(expirate)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEYS.getEncoded());
    }
}
