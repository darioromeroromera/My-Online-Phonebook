package com.rest.pruebarest.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rest.pruebarest.exceptions.BadBodyException;
import com.rest.pruebarest.helpers.CheckerHelper;
import com.rest.pruebarest.helpers.ResponseHelper;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.UserRepo;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
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
        HashMap<String, Object> response = new HashMap<>();

        try {
            CheckerHelper.checkRegisterParams(user);
        } catch (BadBodyException e) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse(e.getMessage()));
        }

        user.setToken(null);

        user.setEnabled(true);
        User foundUser = null;
        try {
            foundUser = userRepo.findByUsername(user.getUsername());
        } catch (IncorrectResultSizeDataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseHelper.getErrorResponse(
                            "Error fatal, hay varios usuarios con esas credenciales, cuando deberían ser únicos"));
        }

        if (foundUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseHelper.getErrorResponse("Ese usuario ya existe en el sistema"));
        }

        User foundEmailUser = null;

        try {
            foundEmailUser = userRepo.findByEmail(user.getEmail());
        } catch (IncorrectResultSizeDataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseHelper.getErrorResponse(
                            "Error fatal, hay varios usuarios con esas credenciales, cuando deberían ser únicos"));
        }

        if (foundEmailUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseHelper.getErrorResponse("Ese correo ya pertenece a un usuario del sistema"));
        }

        user.setProfilePicture(null);

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);

        response.put("result", "ok");
        response.put("picture", user.getProfilePicture());
        return ResponseEntity.ok(response);

    }

    @RequestMapping
    public ResponseEntity badMethod() {
        return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("Verbo HTTP incorrecto"));
    }

}
