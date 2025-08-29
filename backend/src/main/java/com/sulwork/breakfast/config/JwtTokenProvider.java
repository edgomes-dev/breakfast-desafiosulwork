package com.sulwork.breakfast.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    // A chave secreta é lida do application.properties
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    // O tempo de expiração é lido do application.properties
    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    // A chave decodificada e segura para assinar/validar o token
    private final Key key;

    // O construtor é usado para decodificar a chave uma única vez na inicialização
    public JwtTokenProvider(@Value("${app.jwtSecret}") String jwtSecret) {
        // Decodifica a string Base64 da chave e cria um objeto Key seguro
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Gera um token JWT com base na autenticação do usuário.
     * 
     * @param authentication O objeto de autenticação do Spring Security.
     * @return O token JWT em formato de string.
     */
    public String generateToken(Authentication authentication) {
        // Pega as roles (autoridades) do usuário e junta em uma string
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Define a data de expiração do token
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // Constrói o token JWT
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("roles", roles) // Adiciona as roles como um claim personalizado
                .setIssuedAt(new Date()) // Data de emissão
                .setExpiration(expiryDate) // Data de expiração
                .signWith(key, SignatureAlgorithm.HS512) // Assina o token com a chave segura
                .compact();
    }

    /**
     * Obtém o nome de usuário (subject) do token JWT.
     * 
     * @param token O token JWT.
     * @return O nome de usuário.
     */
    public String getUsernameFromJWT(String token) {
        // Analisa o token usando a chave segura e extrai as claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}