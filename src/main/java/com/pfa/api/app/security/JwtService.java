package com.pfa.api.app.security;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class JwtService {
    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private  String SECRET_KEY;
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    List<String> roles = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    Map<String, Object> claims = new HashMap<>(extraClaims);
    claims.put("roles", roles);

    return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + (1000 * 3600 * 24)))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
}

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        System.out.println("username from the isTokenValid : " + username);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String generatePasswordChangeToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("passwordChange", true);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getInscriptionNumber())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public User validatePasswordChangeToken(String token) {
        Claims claims = extractAllClaims(token);
        if(!claims.containsKey("passwordChange")) {
            throw new JwtException("Invalid token type");
        }
        return userRepository.findByInscriptionNumber(claims.getSubject())
                .orElseThrow(() -> new JwtException("User not found"));
    }
    public String generateTempToken(User user) {
        return Jwts.builder()
                .setSubject(user.getInscriptionNumber())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15min
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Utilisez getSigningKey() au lieu de SECRET_KEY
                .compact();
    }

    public User validateTempToken(String token) {
        Claims claims = extractAllClaims(token); // Utilisez la méthode existante
        return userRepository.findByInscriptionNumber(claims.getSubject())
                .orElseThrow(() -> new JwtException("Token invalide"));
    }


}