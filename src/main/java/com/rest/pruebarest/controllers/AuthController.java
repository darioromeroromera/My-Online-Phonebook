package com.rest.pruebarest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import com.rest.pruebarest.exceptions.LoginBadBodyException;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.UserRepo;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepo userRepo;

    @PostMapping
    public ResponseEntity login(@RequestBody @Nullable User user) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            CheckerHelper.checkLoginParams(user);
        } catch (LoginBadBodyException e) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse(e.getMessage()));
        }

        User foundUser = userRepo.findByUsername(user.getUsername());

        PasswordEncoder encoder = new BCryptPasswordEncoder();

        if (foundUser == null || !encoder.matches(user.getPassword(), foundUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseHelper.getErrorResponse("Credenciales incorrectas"));
        }

        String token = JWTHelper.generateToken(foundUser.getId(), foundUser.getUsername());

        foundUser.setToken(token);

        userRepo.save(foundUser);

        response.put("result", "ok");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @RequestMapping
    public ResponseEntity badMethod() {
        return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("Verbo HTTP incorrecto"));
    }
}
