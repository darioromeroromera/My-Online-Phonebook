package com.rest.pruebarest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.rest.pruebarest.models.Contact;
import com.rest.pruebarest.repos.ContactRepo;

import io.jsonwebtoken.Jwt;
import io.micrometer.common.lang.Nullable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactRepo contactRepo;

    @GetMapping
    public ResponseEntity getAll(@RequestHeader("token") @Nullable String token) {
        HashMap<String, Object> response = new HashMap<>();

        if (token == null) {
            response.put("result", "error");
            response.put("details", "El token no ha sido especificado");
            return ResponseEntity.badRequest().body(response);
        }

        if (!JWTHelper.verifyToken(token)) {
            response.put("result", "error");
            response.put("details", "El token ha sido manipulado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Long userId = JWTHelper.getUserId(token);

            List<Contact> contacts = contactRepo.findByUserId(userId);
            response.put("result", "ok");
            response.put("data", contacts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("result", "error");
            response.put("details", "Error extrayendo los datos del token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity getOne(@PathVariable String id, @RequestHeader("token") @Nullable String token) {
        HashMap<String, Object> response = new HashMap<>();

        if (token == null) {
            response.put("result", "error");
            response.put("details", "El token no ha sido especificado");
            return ResponseEntity.badRequest().body(response);
        }

        if (!id.matches("\\d+")) {
            response.put("result", "error");
            response.put("details", "El id debe ser un número");
            // Aquí es donde necesito la respuesta errónea
            return ResponseEntity.badRequest().body(response);
        }
        // Sí es un número y hace la petición bien
        Optional<Contact> oContacto = contactRepo.findById(Long.parseLong(id));
        if (oContacto.isPresent()) {
            response.put("result", "ok");
            response.put("data", oContacto.get());
            return ResponseEntity.ok(response);
        }

        response.put("result", "error");
        response.put("details", "El contacto no existe");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping
    public ResponseEntity saveContact(@RequestBody @Nullable Contact contact,
            @RequestHeader("token") @Nullable String token) {
        HashMap<String, Object> response = new HashMap<>();

        if (token == null) {
            response.put("result", "error");
            response.put("details", "El token no ha sido especificado");
            return ResponseEntity.badRequest().body(response);
        }

        if (contact == null) {
            response.put("result", "error");
            response.put("details", "El body no puede estar vacío");
            return ResponseEntity.badRequest().body(response);
        }

        if (contact.getId() != null) {
            response.put("result", "error");
            response.put("details", "El id no puede estar especificado");
            return ResponseEntity.badRequest().body(response);
        }

        if (contact.getContactName() == null) {
            response.put("result", "error");
            response.put("details", "El campo contact_name es obligatorio");
            return ResponseEntity.badRequest().body(response);
        }

        if (contact.getFullName() == null) {
            response.put("result", "error");
            response.put("details", "El campo full_name es obligatorio");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Contact savedContact = contactRepo.save(contact);
            response.put("result", "ok");
            response.put("insert_id", savedContact.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (DataAccessException e) {
            response.put("result", "error");
            response.put("details", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @RequestMapping
    public ResponseEntity badMethod() {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "error");
        response.put("details", "Verbo HTTP incorrecto.");
        return ResponseEntity.badRequest().body(response);
    }

    @RequestMapping("/{id}")
    public ResponseEntity badIdMethod() {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "error");
        response.put("details", "Verbo HTTP incorrecto.");
        return ResponseEntity.badRequest().body(response);
    }
}
