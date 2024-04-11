package com.rest.pruebarest.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.rest.pruebarest.exceptions.ImageBadFormatException;
import com.rest.pruebarest.exceptions.ImageUploadErrorException;
import com.rest.pruebarest.exceptions.NoImageException;
import com.rest.pruebarest.exceptions.TokenException;
import com.rest.pruebarest.models.Contact;
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
    public ResponseEntity getAll(@RequestHeader("token") @Nullable String token) {
        HashMap<String, Object> response = new HashMap<>();

        if (token == null || !JWTHelper.verifyToken(token)) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El token no es válido"));
        }

        try {
            Long userId = JWTHelper.getUserId(token);

            JWTHelper.checkTokenMatching(userId, token);

            List<Contact> contacts = contactRepo.findByUserId(userId);
            response.put("result", "ok");
            response.put("data", contacts);
            return ResponseEntity.ok(response);
        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseHelper.getErrorResponse(e.getMessage()));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseHelper.getErrorResponse("Error procesando el token"));
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

        if (token == null || !JWTHelper.verifyToken(token)) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El token no es válido"));
        }

        if (contact == null) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El body no puede estar vacío"));
        }

        if (contact.getId() != null) {
            return ResponseEntity.badRequest()
                    .body(ResponseHelper.getErrorResponse("El id no puede estar especificado"));
        }

        if (contact.getContactName() == null) {
            return ResponseEntity.badRequest()
                    .body(ResponseHelper.getErrorResponse("El campo contact_name es obligatorio"));
        }

        if (contact.getFullName() == null) {
            return ResponseEntity.badRequest()
                    .body(ResponseHelper.getErrorResponse("El campo full_name es obligatorio"));
        }

        try {
            Long userId = JWTHelper.getUserId(token);

            JWTHelper.checkTokenMatching(userId, token);

            contact.setUserId(userId);

            if (contact.getContactPicture() != null) {
                try {
                    changeContactPicture(contact);
                } catch (ImageBadFormatException e) {
                    response.put("result", "error");
                    response.put("details", e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                } catch (ImageUploadErrorException e) {
                    response.put("result", "error");
                    response.put("details", e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            }

            Contact savedContact = contactRepo.save(contact);
            response.put("result", "ok");
            response.put("insert_id", savedContact.getId());
            response.put("picture", savedContact.getContactPicture());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseHelper.getErrorResponse(e.getMessage()));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseHelper.getErrorResponse("Error procesando el token"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateContact(@PathVariable String id, @RequestBody @Nullable Contact newContact,
            @RequestHeader("token") @Nullable String token) {
        HashMap<String, Object> response = new HashMap<>();

        if (token == null) {
            response.put("result", "error");
            response.put("details", "El token no ha sido especificado");
            return ResponseEntity.badRequest().body(response);
        }

        if (!id.matches("\\d+")) {
            response.put("result", "error");
            response.put("details", "El id debe ser un número");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Contact> oContacto = contactRepo.findById(Long.parseLong(id));

        if (!oContacto.isPresent()) {
            response.put("result", "error");
            response.put("details", "El contacto con el id especificado no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Contact oldContact = oContacto.get();

        if (newContact == null) {
            response.put("result", "error");
            response.put("details", "El body no puede estar vacío");
            return ResponseEntity.badRequest().body(response);
        }

        if (newContact.getId() != null) {
            response.put("result", "error");
            response.put("details", "El id no puede estar especificado");
            return ResponseEntity.badRequest().body(response);
        }

        if (newContact.getContactName() == null) {
            response.put("result", "error");
            response.put("details", "El campo contact_name es obligatorio");
            return ResponseEntity.badRequest().body(response);
        }

        if (newContact.getFullName() == null) {
            response.put("result", "error");
            response.put("details", "El campo full_name es obligatorio");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Long userId = JWTHelper.getUserId(token);

            Optional<User> oUser = userRepo.findById(userId);

            if (!oUser.isPresent()) {
                response.put("result", "error");
                response.put("details", "El id del usuario en el token no corresponde con ningún usuario existente");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!oUser.get().getToken().equals(token)) {
                response.put("result", "error");
                response.put("details", "El token introducido no corresponde con el token del usuario");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (oldContact.getUserId() != userId) {
                response.put("result", "error");
                response.put("details", "Permiso denegado. Este contacto no pertenece a su usuario");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            newContact.setUserId(userId);

            newContact.setId(oldContact.getId());

            if (newContact.getContactPicture() != null) {
                try {
                    changeContactPicture(newContact);
                } catch (ImageBadFormatException e) {
                    response.put("result", "error");
                    response.put("details", e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                } catch (ImageUploadErrorException e) {
                    response.put("result", "error");
                    response.put("details", e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            } else if (oldContact.getContactPicture() != null)
                newContact.setContactPicture(oldContact.getContactPicture());

            Contact updatedContact = contactRepo.save(newContact);
            response.put("result", "ok");
            response.put("picture", updatedContact.getContactPicture());
            return ResponseEntity.ok(response);

        } catch (DataAccessException e) {
            response.put("result", "error");
            response.put("details", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (JsonProcessingException e) {
            response.put("result", "error");
            response.put("details", "Error extrayendo los datos del token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteContact(@PathVariable String id, @RequestHeader("token") @Nullable String token) {
        HashMap<String, Object> response = new HashMap<>();

        if (token == null) {
            response.put("result", "error");
            response.put("details", "El token no ha sido especificado");
            return ResponseEntity.badRequest().body(response);
        }

        if (!id.matches("\\d+")) {
            response.put("result", "error");
            response.put("details", "El id debe ser un número");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Contact> oContacto = contactRepo.findById(Long.parseLong(id));

        if (!oContacto.isPresent()) {
            response.put("result", "error");
            response.put("details", "El contacto con el id especificado no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Contact contact = oContacto.get();

        try {
            Long userId = JWTHelper.getUserId(token);

            Optional<User> oUser = userRepo.findById(userId);

            if (!oUser.isPresent()) {
                response.put("result", "error");
                response.put("details", "El id del usuario en el token no corresponde con ningún usuario existente");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!oUser.get().getToken().equals(token)) {
                response.put("result", "error");
                response.put("details", "El token introducido no corresponde con el token del usuario");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (contact.getUserId() != userId) {
                response.put("result", "error");
                response.put("details", "Permiso denegado. Este contacto no pertenece a su usuario");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            if (contact.getContactPicture() != null) {
                try {
                    deleteContactPicture(contact);
                } catch (NoImageException e) {
                    response.put("result", "error");
                    response.put("details", e.getMessage());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                } catch (ImageBadFormatException e) {
                    response.put("result", "error");
                    response.put("details", e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            }

            contactRepo.delete(contact);
            response.put("result", "ok");

            return ResponseEntity.ok(response);

        } catch (DataAccessException e) {
            response.put("result", "error");
            response.put("details", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (JsonProcessingException e) {
            response.put("result", "error");
            response.put("details", "Error extrayendo los datos del token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
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
    public void badIdMethod() {
        badMethod();
    }

    public void changeContactPicture(Contact contact) throws ImageBadFormatException, ImageUploadErrorException {
        String[] tokens = contact.getContactPicture().split(",");
        if (tokens.length != 2) {
            throw new ImageBadFormatException("Los datos de la imagen deben llevar solo una coma obligatoriamente");
        }

        Pattern patron = Pattern.compile("data:image/(jpeg|png);base64");

        Matcher matcher = patron.matcher(tokens[0].trim());

        if (!matcher.find()) {
            throw new ImageBadFormatException("La imagen debe ser formato jpeg o png");
        }

        byte[] imgBytes = Base64.getDecoder().decode(tokens[1]);

        String filename = ImageHelper.generateFilename(matcher.group(1));

        try {
            String route = "src/main/resources/static/" + filename;
            Files.write(Paths.get(route), imgBytes);
            contact.setContactPicture("http://localhost:8080/" + filename);
        } catch (IOException e) {
            throw new ImageUploadErrorException("Ha ocurrido un error intentando subir la imagen");
        }
    }

    public void deleteContactPicture(Contact contact) throws NoImageException, ImageBadFormatException {
        if (contact.getContactPicture() == null) {
            throw new NoImageException("El contacto no tiene una imagen para poder borrarla");
        }

        Pattern pattern = Pattern.compile("http://localhost:8080/(.+)");

        Matcher matcher = pattern.matcher(contact.getContactPicture());

        if (!matcher.matches()) {
            throw new ImageBadFormatException(
                    "El campo contact_picture del contacto no tiene el formato correcto. Poniendo el campo a vacío");
        }

        contact.setContactPicture(null);
        contactRepo.save(contact);

        String route = "src/main/resources/static/" + matcher.group(1);

        File imgFile = new File(route);

        if (imgFile.exists()) {
            imgFile.delete();
        } else {
            throw new NoImageException("La imagen que tenía asociada el contacto no existe. Poniendo el campo vacío.");
        }
    }

}
