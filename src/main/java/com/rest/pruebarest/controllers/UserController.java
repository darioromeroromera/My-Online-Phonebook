package com.rest.pruebarest.controllers;

import java.security.DrbgParameters.Reseed;
import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;

import org.hibernate.query.results.ResultsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.rest.pruebarest.models.ChangePasswordRequest;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.UserRepo;

import io.micrometer.common.lang.Nullable;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepo userRepo;

    @GetMapping("/profile-picture")
    public ResponseEntity getProfilePicture(@RequestHeader("token") @Nullable String token) {
        if (token == null || !JWTHelper.verifyToken(token)) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El token no es válido"));
        }

        Long userId;
        try {
            userId = JWTHelper.getUserId(token);
            
            JWTHelper.checkTokenMatching(userId, token);

            Optional<User> oUser = userRepo.findById(userId);
    
            if (!oUser.isPresent())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseHelper.getErrorResponse("El usuario indicado en el token no existe"));
    
            User foundUser = oUser.get();

            HashMap<String, Object> response = new HashMap<>();

            response.put("result", "ok");
            response.put("picture", foundUser.getProfilePicture());
            CacheControl cacheControl = CacheControl.noCache().noStore().mustRevalidate().maxAge(Duration.ZERO);
            return ResponseEntity.status(HttpStatus.OK).cacheControl(cacheControl).body(response);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseHelper.getErrorResponse("Error procesando el token"));
        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseHelper.getErrorResponse(e.getMessage()));
        }
    }


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

            CacheControl cacheControl = CacheControl.noCache().noStore().mustRevalidate().maxAge(Duration.ZERO);
            return ResponseEntity.status(HttpStatus.OK).cacheControl(cacheControl).body(response);

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

    @PutMapping("/change-password")
    public ResponseEntity updatePassword(@RequestHeader("token") @Nullable String token, @RequestBody @Nullable ChangePasswordRequest request) {
        try {
            CheckerHelper.checkChangePasswordParams(request);
        } catch (BadBodyException e) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse(e.getMessage()));
        }

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

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (!encoder.matches(request.getOldPassword(), foundUser.getPassword()))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseHelper.getErrorResponse("La antigua contraseña enviada no es correcta"));

            try {
                CheckerHelper.checkPassword(request.getNewPassword());
            } catch (BadBodyException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseHelper.getErrorResponse("La nueva contraseña no cumple con los parámetros necesarios"));
            }

            foundUser.setPassword(encoder.encode(request.getNewPassword()));

            userRepo.save(foundUser);

            HashMap<String, Object> response = new HashMap<>();

            response.put("result", "ok");

            return ResponseEntity.ok(response);

        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseHelper.getErrorResponse(e.getMessage()));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseHelper.getErrorResponse("Error procesando el token"));
        }
    }

    @RequestMapping("/profile-picture")
    public ResponseEntity badMethod() {
        return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("Verbo HTTP incorrecto"));
    }

    @RequestMapping("/change-password")
    public ResponseEntity badPasswordMethod() {
        return badMethod();
    }
}
