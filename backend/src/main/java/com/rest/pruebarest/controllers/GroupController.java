package com.rest.pruebarest.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
import com.rest.pruebarest.models.ContactGroup;
import com.rest.pruebarest.repos.ContactGroupRepo;
import com.rest.pruebarest.repos.ContactRepo;

import io.micrometer.common.lang.Nullable;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    @Autowired
    ContactGroupRepo groupRepo;

    @Autowired
    ContactRepo contactRepo;

    @GetMapping
    public ResponseEntity getAll(@RequestHeader("token") @Nullable String token) {
        try {
            Long userId = JWTHelper.getUserIdFromToken(token);
            List<ContactGroup> groups = groupRepo.findByUserIdOrderByName(userId);
            return ResponseHelper.buildSuccessfulDataResponseEntity(groups);
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getOne(@RequestHeader("token") @Nullable String token, @Nullable @PathVariable String id) {
        try {
            CheckerHelper.checkIdFormat(id);
            Long userId = JWTHelper.getUserIdFromToken(token);
            ContactGroup foundGroup = findGroupByIdAndUserId(Long.parseLong(id), userId);
            return ResponseHelper.buildSuccessfulDataResponseEntity(foundGroup);
        } catch(Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }
    
    @GetMapping("/{id}/contacts")
    public ResponseEntity getContactsFromGroup(@RequestHeader("token") @Nullable String token, @Nullable @PathVariable String id) {
        try {
            CheckerHelper.checkIdFormat(id);
            Long userId = JWTHelper.getUserIdFromToken(token);
            List<Contact> contacts = findGroupContactsByIdAndUserId(Long.parseLong(id), userId);
            return ResponseHelper.buildSuccessfulDataResponseEntity(contacts);
        } catch(Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @PostMapping
    public ResponseEntity saveGroup(@RequestBody @Nullable ContactGroup group,
            @RequestHeader("token") @Nullable String token) {
        try {
            CheckerHelper.checkGroupParams(group);
            Long userId = JWTHelper.getUserIdFromToken(token);
            group.setUserId(userId);
            CheckerHelper.checkGroupCollision(group);
            ContactGroup savedGroup = groupRepo.save(group);
            return ResponseHelper.buildSuccessfulDataResponseEntity(savedGroup);
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateGroup(@PathVariable String id, @RequestBody @Nullable ContactGroup newGroup,
        @RequestHeader("token") @Nullable String token) {      
        try {
            CheckerHelper.checkIdFormat(id);
            CheckerHelper.checkGroupParams(newGroup);
            Long userId = JWTHelper.getUserIdFromToken(token);
            ContactGroup oldGroup = findGroupByIdAndUserId(Long.parseLong(id), userId);
            setNewGroupFieldsAndSave(oldGroup, newGroup);
            return ResponseHelper.buildSuccessfulResponseEntity();
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteGroup(@PathVariable String id, @RequestHeader("token") @Nullable String token) {
        try {
            CheckerHelper.checkIdFormat(id);
            Long userId = JWTHelper.getUserIdFromToken(token);
            ContactGroup group = findGroupByIdAndUserId(Long.parseLong(id), userId);
            groupRepo.delete(group);
            return ResponseHelper.buildSuccessfulResponseEntity();
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

    @RequestMapping("/{id}/contacts")
    public ResponseEntity badIdContactsMethod() {
        return badMethod();
    }

    private ContactGroup findGroupByIdAndUserId(Long id, Long userId) throws NotFoundException, ForbiddenAccessException {
        Optional<ContactGroup> oGroup = groupRepo.findById(id);

        if (oGroup.isPresent()) {
            ContactGroup group = oGroup.get();
            if (group.getUserId() != userId)
                throw new ForbiddenAccessException("El grupo no pertenece a este usuario");
            return group;
        } else {
            throw new NotFoundException("El grupo con el id especificado no existe");
        }
    }

    private List<Contact> findGroupContactsByIdAndUserId(Long id, Long userId) throws NotFoundException, ForbiddenAccessException {
        Optional<ContactGroup> oGroup = groupRepo.findById(id);

        if (oGroup.isPresent()) {
            ContactGroup group = oGroup.get();
            if (group.getUserId() != userId)
                throw new ForbiddenAccessException("El grupo no pertenece a este usuario");
            return contactRepo.findByGroupId(group.getId());
        } else {
            throw new NotFoundException("El grupo con el id especificado no existe");
        }
    }

    private ContactGroup setNewGroupFieldsAndSave(ContactGroup oldGroup, ContactGroup newGroup) {
        newGroup.setId(oldGroup.getId());
        newGroup.setUserId(oldGroup.getUserId());
        return groupRepo.save(newGroup);
    }
}
