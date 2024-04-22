package com.rest.pruebarest.controllers;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rest.pruebarest.exceptions.BadBodyException;
import com.rest.pruebarest.exceptions.ImageBadFormatException;
import com.rest.pruebarest.exceptions.ImageUploadErrorException;
import com.rest.pruebarest.exceptions.NoImageException;
import com.rest.pruebarest.exceptions.TokenException;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.UserRepo;

import io.micrometer.common.lang.Nullable;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepo userRepo;

    @PutMapping("/profile-picture")
    public ResponseEntity updateProfilePicture(@RequestHeader("token") @Nullable String token, @RequestBody @Nullable User user) {
        try {
            CheckerHelper.checkProfilePictureParams(user);
        } catch (BadBodyException e) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse(e.getMessage()));
        }

        if (token == null || !JWTHelper.verifyToken(token)) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El token no es válido"));
        }

        try {
            Long userId = JWTHelper.getUserId(token);

            JWTHelper.checkTokenMatching(userId, token);

            ImageHelper.changeProfilePicture(user);

            Optional<User> oUser = userRepo.findById(userId);

            if (!oUser.isPresent())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseHelper.getErrorResponse("El usuario indicado en el token no existe"));

            User foundUser = oUser.get();

            if (foundUser.getProfilePicture() != null)
                deleteProfilePicture(token);

            foundUser.setProfilePicture(user.getProfilePicture());

            userRepo.save(foundUser);

            HashMap<String, Object> response = new HashMap<>();

            response.put("result", "ok");
            response.put("picture", foundUser.getProfilePicture());

            return ResponseEntity.ok(response);

        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseHelper.getErrorResponse(e.getMessage()));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseHelper.getErrorResponse("Error procesando el token"));
        } catch (ImageBadFormatException e) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse(e.getMessage()));
        } catch (ImageUploadErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseHelper.getErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/profile-picture")
    public ResponseEntity deleteProfilePicture(@RequestHeader("token") @Nullable String token) {
        if (token == null || !JWTHelper.verifyToken(token)) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El token no es válido"));
        }

        try {
            Long userId = JWTHelper.getUserId(token);

            JWTHelper.checkTokenMatching(userId, token);

            Optional<User> oUser = userRepo.findById(userId);

            if (!oUser.isPresent())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseHelper.getErrorResponse("El usuario indicado en el token no existe"));

            User foundUser = oUser.get();

            ImageHelper.deleteProfilePicture(foundUser);

            HashMap<String, Object> response = new HashMap<>();

            response.put("result", "ok");

            return ResponseEntity.ok(response);

        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseHelper.getErrorResponse(e.getMessage()));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseHelper.getErrorResponse("Error procesando el token"));
        } catch (ImageBadFormatException e) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse(e.getMessage()));
        } catch (NoImageException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseHelper.getErrorResponse(e.getMessage()));
        }
    }

    @RequestMapping("/profile-picture")
    public ResponseEntity badMethod() {
        return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("Verbo HTTP incorrecto"));
    }
}
