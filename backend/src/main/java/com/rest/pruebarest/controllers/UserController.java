package com.rest.pruebarest.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.rest.pruebarest.exceptions.AuthenticationException;
import com.rest.pruebarest.exceptions.BadBodyException;
import com.rest.pruebarest.exceptions.ImageBadFormatException;
import com.rest.pruebarest.exceptions.ImageUploadErrorException;
import com.rest.pruebarest.exceptions.NoImageException;
import com.rest.pruebarest.exceptions.NotFoundException;
import com.rest.pruebarest.exceptions.TokenAuthException;
import com.rest.pruebarest.exceptions.TokenValidationException;
import com.rest.pruebarest.helpers.CheckerHelper;
import com.rest.pruebarest.helpers.ImageHelper;
import com.rest.pruebarest.helpers.JWTHelper;
import com.rest.pruebarest.helpers.ResponseHelper;
import com.rest.pruebarest.models.ChangePasswordRequest;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.ContactGroupRepo;
import com.rest.pruebarest.repos.ContactRepo;
import com.rest.pruebarest.repos.UserRepo;

import io.micrometer.common.lang.Nullable;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    ContactRepo contactRepo;

    @Autowired
    ContactGroupRepo groupRepo;

    private final PasswordEncoder encoder;

    @Autowired
    public UserController(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Autowired
    UserRepo userRepo;

    @GetMapping("/profile-picture")
    public ResponseEntity getProfilePicture(@RequestHeader("token") @Nullable String token) {
        try {
            String picture = fetchProfilePicture(token);
            return ResponseHelper.buildSuccessfulPictureResponseEntity(picture);
        } catch(Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }


    @PutMapping("/profile-picture")
    public ResponseEntity updateProfilePicture(@RequestHeader("token") @Nullable String token, @RequestBody @Nullable User user) {
        try {
            CheckerHelper.checkProfilePictureParams(user);
            Long userId = JWTHelper.getUserIdFromToken(token);

            String picture = setProfilePicture(user, userId);
            return ResponseHelper.buildSuccessfulPictureResponseEntity(picture);
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @DeleteMapping("/profile-picture")
    public ResponseEntity deleteProfilePicture(@RequestHeader("token") @Nullable String token) {
        try {
            Long userId = JWTHelper.getUserIdFromToken(token);
            removeImage(userId);
            return ResponseHelper.buildSuccessfulResponseEntity();
        } catch(Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity updatePassword(@RequestHeader("token") @Nullable String token, @RequestBody @Nullable ChangePasswordRequest request) {
        try {
            CheckerHelper.checkChangePasswordParams(request);
            Long userId = JWTHelper.getUserIdFromToken(token);
            changePassword(request, userId);
            return ResponseHelper.buildSuccessfulResponseEntity();
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity getStatistics(@RequestHeader("token") @Nullable String token) {
        try {
            Long userId = JWTHelper.getUserIdFromToken(token);

            int contactNumber = contactRepo.countContacts(userId);

            int groupNumber = groupRepo.countGroups(userId);

            return ResponseHelper.buildSuccessfulCountingResponse(contactNumber, groupNumber);
        } catch (Exception e) {
            ResponseHelper.buildErrorResponse(e);
        }
        return null;
    }

    @RequestMapping("/profile-picture")
    public ResponseEntity badMethod() {
        return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("Verbo HTTP incorrecto"));
    }

    @RequestMapping("/change-password")
    public ResponseEntity badPasswordMethod() {
        return badMethod();
    }

    @RequestMapping("/statistics")
    public ResponseEntity badStatisticsMethod() {
        return badMethod();
    }

    private String setProfilePicture(User user, Long userId) throws NotFoundException, ImageBadFormatException, ImageUploadErrorException {
        ImageHelper.changeProfilePicture(user);

        User foundUser = getUser(userId);

        deleteProfilePicture(foundUser.getToken());

        foundUser.setProfilePicture(user.getProfilePicture());

        userRepo.save(foundUser);
        return foundUser.getProfilePicture();
    }

    private void removeImage(Long userId) throws NotFoundException, NoImageException, ImageBadFormatException {
        ImageHelper.deleteProfilePicture(getUser(userId));
    }

    private String fetchProfilePicture(String token) throws JsonMappingException, JsonProcessingException, TokenValidationException, TokenAuthException, NotFoundException {
        Long userId = JWTHelper.getUserIdFromToken(token);
        return getUser(userId).getProfilePicture();
    }

    private User getUser(Long userId) throws NotFoundException {
        Optional<User> oUser = userRepo.findById(userId);

        if (!oUser.isPresent())
            throw new NotFoundException("El usuario indicado en el token no existe");

        return oUser.get();
    }

    private void changePassword(ChangePasswordRequest request, Long userId) throws BadBodyException, AuthenticationException, NotFoundException {  
        User foundUser = getUser(userId);
        if (!doesPasswordMatch(request.getOldPassword(), foundUser.getPassword()))
            throw new AuthenticationException("La antigua contraseña enviada no es correcta");

        CheckerHelper.checkPassword(request.getNewPassword());

        foundUser.setPassword(encoder.encode(request.getNewPassword()));

        userRepo.save(foundUser);
    }

    public boolean doesPasswordMatch(String oldPassword, String databasePassword) {
        return (encoder.matches(oldPassword, databasePassword));
    }
}
