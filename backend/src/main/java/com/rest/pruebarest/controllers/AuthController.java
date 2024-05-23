package com.rest.pruebarest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rest.pruebarest.exceptions.AuthenticationException;
import com.rest.pruebarest.helpers.CheckerHelper;
import com.rest.pruebarest.helpers.JWTHelper;
import com.rest.pruebarest.helpers.ResponseHelper;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.UserRepo;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PasswordEncoder encoder;

    @Autowired
    public AuthController(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Autowired
    private UserRepo userRepo;

    @PostMapping
    public ResponseEntity login(@RequestBody @Nullable User user) {
        try {
            CheckerHelper.checkLoginParams(user);
            User foundUser = authenticateUser(user);
            String token = generateAndSaveToken(foundUser);
            return ResponseHelper.buildSuccessfulTokenResponseEntity(token);
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @RequestMapping
    public ResponseEntity badMethod() {
        return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("Verbo HTTP incorrecto"));
    }

    private User authenticateUser(User user) throws AuthenticationException {
        User foundUser = userRepo.findByUsername(user.getUsername());

        if (!isCredentialsCorrect(user, foundUser)) {
            throw new AuthenticationException("Credenciales incorrectas");
        }

        return foundUser;
    }

    private String generateAndSaveToken(User user) {
        String token = JWTHelper.generateToken(user.getId(), user.getUsername(), user.getEmail());
        user.setToken(token);
        userRepo.save(user);
        return token;
    }

    private boolean isCredentialsCorrect(User user, User foundUser) {
        return (foundUser != null && encoder.matches(user.getPassword(), foundUser.getPassword()));
    }
}
