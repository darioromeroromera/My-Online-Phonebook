package com.rest.pruebarest.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rest.pruebarest.exceptions.ConflictException;
import com.rest.pruebarest.helpers.CheckerHelper;
import com.rest.pruebarest.helpers.ResponseHelper;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    private final PasswordEncoder encoder;

    @Autowired
    public RegisterController(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Autowired
    private UserRepo userRepo;

    @PostMapping
    public ResponseEntity register(@RequestBody @Nullable User user) {
        try {
            CheckerHelper.checkRegisterParams(user);
            checkConflict(user);
            saveUser(user);
            return ResponseHelper.buildSuccessfulResponseEntity();
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @RequestMapping
    public ResponseEntity badMethod() {
        return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("Verbo HTTP incorrecto"));
    }


    private void checkConflict(User user) throws IncorrectResultSizeDataAccessException, ConflictException {
        User foundUser = userRepo.findByUsername(user.getUsername());
        if (foundUser != null) {
            throw new ConflictException("Ese usuario ya existe en el sistema");
        }

        User foundEmailUser = userRepo.findByEmail(user.getEmail());
        if (foundEmailUser != null) {
            throw new ConflictException("Ese correo ya pertenece a un usuario del sistema");
        }

        User foundPhoneUser = userRepo.findByTelefono(user.getTelefono());
        if (foundPhoneUser != null)
        throw new ConflictException("Ese teléfono ya pertenece a un usuario del sistema");
    }

    private void saveUser(User user) {
        user.setToken(null);
        user.setEnabled(true);
        user.setProfilePicture(null);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
    }
}
