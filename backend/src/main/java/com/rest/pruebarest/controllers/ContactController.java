package com.rest.pruebarest.controllers;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rest.pruebarest.exceptions.BadBodyException;
import com.rest.pruebarest.exceptions.ContactColisionException;
import com.rest.pruebarest.exceptions.ImageBadFormatException;
import com.rest.pruebarest.exceptions.ImageUploadErrorException;
import com.rest.pruebarest.exceptions.NoImageException;
import com.rest.pruebarest.exceptions.TokenException;
import com.rest.pruebarest.helpers.CheckerHelper;
import com.rest.pruebarest.helpers.ImageHelper;
import com.rest.pruebarest.helpers.JWTHelper;
import com.rest.pruebarest.helpers.ResponseHelper;
import com.rest.pruebarest.models.Contact;
import com.rest.pruebarest.repos.ContactRepo;

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

    @GetMapping
    public ResponseEntity getAll(@RequestHeader("token") @Nullable String token) {
        HashMap<String, Object> response = new HashMap<>();

        if (token == null || !JWTHelper.verifyToken(token)) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El token no es válido"));
        }

        try {
            Long userId = JWTHelper.getUserId(token);

            JWTHelper.checkTokenMatching(userId, token);

            List<Contact> contacts = contactRepo.findByUserIdOrderByContactName(userId);
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
    public ResponseEntity getOne(@RequestHeader("token") @Nullable String token, @Nullable @PathVariable String id) {
        HashMap<String, Object> response = new HashMap<>();

        if (id == null)
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("No se ha especificado ningún id de contacto"));

        if (!id.matches("\\d+"))
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El id debe ser un número"));
        
        if (token == null || !JWTHelper.verifyToken(token)) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El token no es válido"));
        }

        try {
            Long userId = JWTHelper.getUserId(token);

            JWTHelper.checkTokenMatching(userId, token);

            Optional<Contact> oContact = contactRepo.findById(Long.parseLong(id));

            if (oContact.isPresent()) {
                Contact contact = oContact.get();
                if (contact.getUserId() != userId)
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseHelper.getErrorResponse("El id del usuario propietario no coincide con el del usuario del token"));
                response.put("result", "ok");
                response.put("data", contact);
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseHelper.getErrorResponse("El usuario con id solicitado no existe"));
        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseHelper.getErrorResponse(e.getMessage()));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseHelper.getErrorResponse("Error procesando el token"));
        }
    }

    @PostMapping
    public ResponseEntity saveContact(@RequestBody @Nullable Contact contact,
            @RequestHeader("token") @Nullable String token) {
        HashMap<String, Object> response = new HashMap<>();

        if (token == null || !JWTHelper.verifyToken(token)) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El token no es válido"));
        }

        try {
            CheckerHelper.checkContactParams(contact);
        } catch (BadBodyException e) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse(e.getMessage()));
        }

        try {
            Long userId = JWTHelper.getUserId(token);

            JWTHelper.checkTokenMatching(userId, token);

            contact.setUserId(userId);

            try {
                CheckerHelper.checkContactColision(contact);
            } catch (ContactColisionException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseHelper.getErrorResponse(e.getMessage()));
            }

            if (contact.getContactPicture() != null) {
                try {
                    ImageHelper.changeContactPicture(contact);
                } catch (ImageBadFormatException e) {
                    return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse(e.getMessage()));
                } catch (ImageUploadErrorException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ResponseHelper.getErrorResponse(e.getMessage()));
                }
            }

            Contact savedContact = contactRepo.save(contact);
            response.put("result", "ok");
            response.put("insert_id", savedContact.getId());
            response.put("picture", savedContact.getContactPicture());
            CacheControl cacheControl = CacheControl.noCache().noStore().mustRevalidate().maxAge(Duration.ZERO);
            return ResponseEntity.status(HttpStatus.CREATED).cacheControl(cacheControl).body(response);

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

        if (id == null)
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("No se ha especificado ningún id de contacto"));

        if (token == null || !JWTHelper.verifyToken(token)) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El token no es válido"));
        }

        if (!id.matches("\\d+")) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El id debe ser un número"));
        }

        Optional<Contact> oContacto = contactRepo.findById(Long.parseLong(id));

        if (!oContacto.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseHelper.getErrorResponse("El contacto con el id especificado no existe"));
        }

        Contact oldContact = oContacto.get();

        try {
            CheckerHelper.checkContactParams(newContact);
        } catch (BadBodyException e) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse(e.getMessage()));
        }

        try {
            Long userId = JWTHelper.getUserId(token);

            JWTHelper.checkTokenMatching(userId, token);

            if (oldContact.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ResponseHelper.getErrorResponse("Permiso denegado. ESte contacto no pertenece a su usuario"));
            }

            newContact.setUserId(userId);

            newContact.setId(oldContact.getId());

            if (newContact.getContactPicture() != null) {
                try {
                    if (oldContact.getContactPicture() != null)
                        ImageHelper.deleteContactPicture(oldContact);
                    ImageHelper.changeContactPicture(newContact);
                } catch (ImageBadFormatException e) {
                    return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse(e.getMessage()));
                } catch (ImageUploadErrorException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ResponseHelper.getErrorResponse(e.getMessage()));
                } catch (NoImageException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ResponseHelper.getErrorResponse(e.getMessage()));
                }
            } else if (oldContact.getContactPicture() != null)
                newContact.setContactPicture(oldContact.getContactPicture());

            Contact updatedContact = contactRepo.save(newContact);
            response.put("result", "ok");
            response.put("picture", updatedContact.getContactPicture());
            CacheControl cacheControl = CacheControl.noCache().noStore().mustRevalidate().maxAge(Duration.ZERO);
            return ResponseEntity.status(HttpStatus.OK).cacheControl(cacheControl).body(response);

        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseHelper.getErrorResponse(e.getLocalizedMessage()));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseHelper.getErrorResponse("Error extrayendo los datos del token"));
        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseHelper.getErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteContact(@PathVariable String id, @RequestHeader("token") @Nullable String token) {
        HashMap<String, Object> response = new HashMap<>();

        if (id == null)
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("No se ha especificado ningún id de contacto"));

        if (token == null) {
            return ResponseEntity.badRequest()
                    .body(ResponseHelper.getErrorResponse("El token no ha sido especificado"));
        }

        if (!id.matches("\\d+")) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El id debe ser un número"));
        }

        Optional<Contact> oContacto = contactRepo.findById(Long.parseLong(id));

        if (!oContacto.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseHelper.getErrorResponse("El contacto con el id especificado no existe"));
        }

        Contact contact = oContacto.get();

        try {
            Long userId = JWTHelper.getUserId(token);

            JWTHelper.checkTokenMatching(userId, token);

            if (contact.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ResponseHelper.getErrorResponse("Permiso denegado. Este contacto no pertenece a su usuario"));
            }

            if (contact.getContactPicture() != null) {
                try {
                    ImageHelper.deleteContactPicture(contact);
                } catch (NoImageException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ResponseHelper.getErrorResponse(e.getMessage()));
                } catch (ImageBadFormatException e) {
                    return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse(e.getMessage()));
                }
            }

            contactRepo.delete(contact);
            response.put("result", "ok");

            return ResponseEntity.ok(response);

        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseHelper.getErrorResponse(e.getLocalizedMessage()));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseHelper.getErrorResponse("Error extrayendo los datos del token"));
        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseHelper.getErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/picture")
    public ResponseEntity deleteContactPicture(@PathVariable String id, @RequestHeader("token") @Nullable String token) {
        HashMap<String, Object> response = new HashMap<>();

        if (id == null)
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("No se ha especificado ningún id de contacto"));

        if (token == null) {
            return ResponseEntity.badRequest()
                    .body(ResponseHelper.getErrorResponse("El token no ha sido especificado"));
        }

        if (!id.matches("\\d+")) {
            return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("El id debe ser un número"));
        }

        Optional<Contact> oContacto = contactRepo.findById(Long.parseLong(id));

        if (!oContacto.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseHelper.getErrorResponse("El contacto con el id especificado no existe"));
        }

        Contact contact = oContacto.get();

        try {
            Long userId = JWTHelper.getUserId(token);

            JWTHelper.checkTokenMatching(userId, token);

            if (contact.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ResponseHelper.getErrorResponse("Permiso denegado. Este contacto no pertenece a su usuario"));
            }

            try {
                ImageHelper.deleteContactPicture(contact);
            } catch (NoImageException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ResponseHelper.getErrorResponse(e.getMessage()));
            } catch (ImageBadFormatException e) {
                return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse(e.getMessage()));
            }
            response.put("result", "ok");
            response.put("data", contact);

            return ResponseEntity.ok(response);

        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseHelper.getErrorResponse(e.getLocalizedMessage()));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseHelper.getErrorResponse("Error extrayendo los datos del token"));
        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseHelper.getErrorResponse(e.getMessage()));
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
}
