package com.rest.pruebarest.controllers;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.boot.jackson.JsonObjectDeserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.pruebarest.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwe;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JWTHelper {

    private static String SECRET_KEY = "Una clave super secreta que no se puede revelar";

    public static String generateToken(Long id, String username) {
        byte[] bKey = SECRET_KEY.getBytes(StandardCharsets.UTF_8);

        String token = Jwts.builder().claim("username", username).claim("id", id)
                .issuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(bKey), Jwts.SIG.HS256).compact();
        return token;
    }

    public static boolean verifyToken(String token) {
        String[] tokens = token.split("\\.");
        if (tokens.length < 3) {
            return false;
        }

        byte[] bKey = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        SecretKey key = Keys.hmacShaKeyFor(bKey);
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(key)
                .build();
        try {
            jwtParser.parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Long getUserId(String token) throws JsonMappingException, JsonProcessingException {
        String[] tokens = token.split("\\.");
        String body = new String(Base64.getUrlDecoder().decode(tokens[1]), StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode nameNode = mapper.readTree(body);

        return nameNode.get("id").asLong();
    }
}
