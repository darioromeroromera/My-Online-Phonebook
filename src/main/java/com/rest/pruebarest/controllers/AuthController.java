package com.rest.pruebarest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.UserRepo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepo userRepo;

    private String generateToken(Long id, String username) {
        String sKey = "Una clave super secreta que no se puede revelar";
        LocalDateTime date = LocalDateTime.now();
        byte[] bKey = sKey.getBytes(StandardCharsets.UTF_8);

        String token = Jwts.builder().claim("username", username).claim("id", id).issuedAt(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(Keys.hmacShaKeyFor(bKey), Jwts.SIG.HS256).compact();
        return token;
    }

    @PostMapping
    public ResponseEntity login(@RequestBody @Nullable User user) {
        HashMap<String, Object> response = new HashMap<>();
        
        if (user == null) {
            response.put("result", "error");
            response.put("details", "El body no puede estar vacío");
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getUsername() == null) {
            response.put("result", "error");
            response.put("details", "El campo username es obligatorio");
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getPassword() == null) {
            response.put("result", "error");
            response.put("details", "El campo contraseña es obligatorio");
            return ResponseEntity.badRequest().body(response);
        }

        User foundUser = userRepo.findByUsername(user.getUsername());
        if (foundUser == null) {
            response.put("result", "error");
            response.put("details", "Credenciales incorrectas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        PasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(user.getPassword(), foundUser.getPassword())) {
            response.put("result", "error");
            response.put("details", "Credenciales incorrectas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = generateToken(foundUser.getId(), foundUser.getUsername());

        foundUser.setToken(token);

        userRepo.save(foundUser);

        response.put("result", "ok");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }


    @RequestMapping
    public ResponseEntity badMethod() {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "error");
        response.put("details", "Verbo HTTP incorrecto.");
        return ResponseEntity.badRequest().body(response);
    }
}
