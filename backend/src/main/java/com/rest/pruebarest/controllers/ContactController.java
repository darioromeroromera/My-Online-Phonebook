package com.rest.pruebarest.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rest.pruebarest.exceptions.ForbiddenAccessException;
import com.rest.pruebarest.exceptions.ImageBadFormatException;
import com.rest.pruebarest.exceptions.ImageUploadErrorException;
import com.rest.pruebarest.exceptions.NoImageException;
import com.rest.pruebarest.exceptions.NotFoundException;
import com.rest.pruebarest.helpers.CheckerHelper;
import com.rest.pruebarest.helpers.ImageHelper;
import com.rest.pruebarest.helpers.JWTHelper;
import com.rest.pruebarest.helpers.ResponseHelper;
import com.rest.pruebarest.models.Contact;
import com.rest.pruebarest.models.Message;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.ContactRepo;
import com.rest.pruebarest.repos.UserRepo;

import io.micrometer.common.lang.Nullable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public ResponseEntity getAll(@RequestHeader("Bearer") @Nullable String token) {
        try {
            Long userId = JWTHelper.getUserIdFromToken(token);
            List<Contact> contacts = contactRepo.findByUserIdOrderByContactName(userId);
            return ResponseHelper.buildSuccessfulDataResponseEntity(contacts);
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getOne(@RequestHeader("Bearer") @Nullable String token, @Nullable @PathVariable String id) {
        try {
            CheckerHelper.checkIdFormat(id);
            Long userId = JWTHelper.getUserIdFromToken(token);
            Contact foundContact = findContactByIdAndUserId(Long.parseLong(id), userId);
            return ResponseHelper.buildSuccessfulDataResponseEntity(foundContact);
        } catch(Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @PostMapping
    public ResponseEntity saveContact(@RequestBody @Nullable Contact contact,
            @RequestHeader("Bearer") @Nullable String token) {
        try {
            CheckerHelper.checkContactParams(contact);
            Long userId = JWTHelper.getUserIdFromToken(token);
            contact.setUserId(userId);
            CheckerHelper.checkContactColision(contact);
            if (contact.getGroupId() != null)
                CheckerHelper.checkGroupAuthorization(contact.getGroupId(), userId);
            if (contact.getContactPicture() != null)
                ImageHelper.changeContactPicture(contact);
            Contact savedContact = contactRepo.save(contact);
            return ResponseHelper.buildSuccessfulInsertIdAndPictureResponse(savedContact.getId(), savedContact.getContactPicture());
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateContact(@PathVariable String id, @RequestBody @Nullable Contact newContact,
            @RequestHeader("Bearer") @Nullable String token) {
                
        try {
            CheckerHelper.checkIdFormat(id);
            Long userId = JWTHelper.getUserIdFromToken(token);
            Contact oldContact = findContactByIdAndUserId(Long.parseLong(id), userId);
            CheckerHelper.checkContactParams(newContact);
            if (newContact.getGroupId() != null)
                CheckerHelper.checkGroupAuthorization(newContact.getGroupId(), userId);
            Contact updatedContact = setNewContactFieldsAndSave(oldContact, newContact);
            return ResponseHelper.buildSuccessfulPictureResponseEntity(updatedContact.getContactPicture());
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteContact(@PathVariable String id, @RequestHeader("Bearer") @Nullable String token) {
        try {
            CheckerHelper.checkIdFormat(id);
            Long userId = JWTHelper.getUserIdFromToken(token);
            Contact contact = findContactByIdAndUserId(Long.parseLong(id), userId);
            if (contact.getContactPicture() != null)
                ImageHelper.deleteContactPicture(contact);
            contactRepo.delete(contact);
            return ResponseHelper.buildSuccessfulResponseEntity();
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @DeleteMapping("/{id}/picture")
    public ResponseEntity deleteContactPicture(@PathVariable String id, @RequestHeader("Bearer") @Nullable String token) {
        try {
            CheckerHelper.checkIdFormat(id);
            Long userId = JWTHelper.getUserIdFromToken(token);
            Contact contact = findContactByIdAndUserId(Long.parseLong(id), userId);
            ImageHelper.deleteContactPicture(contact);
            return ResponseHelper.buildSuccessfulDataResponseEntity(contact);
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @GetMapping("/availables")
    public ResponseEntity getContactsAvailableForMessaging(@RequestHeader("Bearer") @Nullable String token) {
        try {
            Long userId = JWTHelper.getUserIdFromToken(token);
            List<Contact> allContacts = contactRepo.findByUserIdOrderByContactName(userId);
            List<Contact> availableContacts = new ArrayList<>();

            for (Contact contact : allContacts) {
                User user = userRepo.findByTelefono(contact.getTelefono());
                if (user != null) {
                    int friendContactCount = contactRepo.countByTelefonoAndUserId(user.getId(), userRepo.findById(userId).get().getTelefono());
                    if (friendContactCount > 0) {
                        availableContacts.add(contact);
                    }
                }
            }
            return ResponseHelper.buildSuccessfulDataResponseEntity(availableContacts);
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @RequestMapping
    public ResponseEntity badMethod() {
        return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("Verbo HTTP incorrecto"));
    }

    @RequestMapping("/{id}")
    public ResponseEntity badIdMethod() {
        return badMethod();
    }

    @RequestMapping("/{id}/picture")
    public ResponseEntity badPictureMethod() {
        return badMethod();
    }

    @RequestMapping("/availables")
    public ResponseEntity badAvailableMethod() {
        return badMethod();
    }

    private Contact findContactByIdAndUserId(Long id, Long userId) throws NotFoundException, ForbiddenAccessException {
        Optional<Contact> oContact = contactRepo.findById(id);

        if (oContact.isPresent()) {
            Contact contact = oContact.get();
            if (contact.getUserId() != userId)
                throw new ForbiddenAccessException("El contacto no pertenece a este usuario");
            return contact;
        } else {
            throw new NotFoundException("El contacto con el id especificado no existe");
        }
    }

    private Contact setNewContactFieldsAndSave(Contact oldContact, Contact newContact) throws ImageBadFormatException, ImageUploadErrorException, NoImageException {
        newContact.setId(oldContact.getId());
        newContact.setUserId(oldContact.getUserId());
        if (newContact.getContactPicture() != null) {
            ImageHelper.changeContactPicture(newContact);
            if (oldContact.getContactPicture() != null)
                ImageHelper.deleteContactPicture(oldContact);
        } else if (oldContact.getContactPicture() != null)
            newContact.setContactPicture(oldContact.getContactPicture());
        return contactRepo.save(newContact);
    }
}
