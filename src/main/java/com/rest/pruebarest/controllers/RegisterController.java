package com.rest.pruebarest.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.UserRepo;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private UserRepo userRepo;

    @PostMapping
    public ResponseEntity register(@RequestBody @Nullable User user) {
        // List<String> mandatoryParams = List.of("email", "nombre", "password");
        HashMap<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("result", "error");
            response.put("details", "El body no puede estar vacío");
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getEmail() == null) {
            response.put("result", "error");
            response.put("details", "El email no puede estar vacío");
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getUsername() == null) {
            response.put("result", "error");
            response.put("details", "El username no puede estar vacío");
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getPassword() == null) {
            response.put("result", "error");
            response.put("details", "El password no puede estar vacío");
            return ResponseEntity.badRequest().body(response);
        }

        user.setEnabled(true);

        if (user.getToken() != null) {
            response.put("result", "error");
            response.put("details", "El token solo puede manipularse cuando se inicia sesión");
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getId() != null) {
            response.put("result", "error");
            response.put("details", "El id no es un parámetro válido");
            return ResponseEntity.badRequest().body(response);
        }

        User foundUser = userRepo.findByUsername(user.getUsername());

        if (foundUser != null) {
            response.put("result", "error");
            response.put("details", "Ese usuario ya existe en el sistema");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);

        response.put("result", "ok");
        response.put("data", user);
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
