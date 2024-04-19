package com.rest.pruebarest.controllers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.pruebarest.exceptions.TokenException;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.UserRepo;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JWTHelper {

    private static UserRepo userRepo;

    private static String SECRET_KEY = "Una clave super secreta que no se puede revelar";

    public static void setUserRepo(UserRepo userRepo) {
        JWTHelper.userRepo = userRepo;
    }

    public static String generateToken(Long id, String username, String email) {
        byte[] bKey = SECRET_KEY.getBytes(StandardCharsets.UTF_8);

        String token = Jwts.builder().claim("username", username).claim("email", email).claim("id", id)
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

    public static void checkTokenMatching(Long userId, String token) throws TokenException {
        Optional<User> oUser = userRepo.findById(userId);

        if (!oUser.isPresent())
            throw new TokenException("El id del usuario en el token no corresponde con ningún usuario existente");

        if (!oUser.get().getToken().equals(token))
            throw new TokenException("El token introducido no corresponde con el token del usuario");
    }
}
