package com.rest.pruebarest.controllers;

import java.security.DrbgParameters.Reseed;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rest.pruebarest.models.Contact;
import com.rest.pruebarest.repos.ContactRepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactRepo contactRepo;

    @GetMapping
    public ResponseEntity<HashMap<String, Object>> getAll() {
        List<Contact> contacts = contactRepo.findAll();
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "ok");
        response.put("data", contacts);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HashMap<String, Object>> pathVar(@PathVariable String id) {
        HashMap<String, Object> response = new HashMap<>();
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
    public Contact saveContact(@RequestBody Contact contact) {
        return contactRepo.save(contact);
    }
}
