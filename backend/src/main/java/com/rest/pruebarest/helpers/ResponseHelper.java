package com.rest.pruebarest.helpers;

import java.time.Duration;
import java.util.HashMap;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rest.pruebarest.exceptions.AuthenticationException;
import com.rest.pruebarest.exceptions.BadBodyException;
import com.rest.pruebarest.exceptions.BadPathVariable;
import com.rest.pruebarest.exceptions.ConflictException;
import com.rest.pruebarest.exceptions.CollisionException;
import com.rest.pruebarest.exceptions.ForbiddenAccessException;
import com.rest.pruebarest.exceptions.ImageBadFormatException;
import com.rest.pruebarest.exceptions.ImageUploadErrorException;
import com.rest.pruebarest.exceptions.NoImageException;
import com.rest.pruebarest.exceptions.NotFoundException;
import com.rest.pruebarest.exceptions.TokenAuthException;
import com.rest.pruebarest.exceptions.TokenValidationException;

public class ResponseHelper {
    public static HashMap<String, Object> getErrorResponse(String details) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "error");
        response.put("details", details);
        return response;
    }

    public static HashMap<String, Object> getSuccessfulResponse() {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "ok");
        return response;
    }

    public static ResponseEntity buildSuccessfulResponseEntity() {
        return ResponseEntity.ok(getSuccessfulResponse());
    }

    public static HashMap<String, Object> getSuccessfulTokenResponse(String token) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "ok");
        response.put("token", token);
        return response;
    }  
    
    public static ResponseEntity buildSuccessfulTokenResponseEntity(String token) {
        return ResponseEntity.ok(getSuccessfulTokenResponse(token));
    }

    public static HashMap<String, Object> getSuccessfulDataResponse(Object data) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "ok");
        response.put("data", data);
        return response;
    }

    public static ResponseEntity buildSuccessfulDataResponseEntity(Object data) {
        return ResponseEntity.ok(getSuccessfulDataResponse(data));
    }

    public static HashMap<String, Object> getSuccessfulPictureResponse(String picture) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "ok");
        response.put("picture", picture);
        return response;
    }

    public static ResponseEntity buildSuccessfulPictureResponseEntity(String picture) {
        return ResponseEntity.status(HttpStatus.OK).cacheControl(getCacheControl()).body(getSuccessfulPictureResponse(picture));
    }

    public static HashMap<String, Object> getSuccessfulCountingResponse(int contactNumber, int groupNumber) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "ok");
        response.put("contact_number", contactNumber);
        response.put("group_number", groupNumber);
        return response;
    }

    public static HashMap<String, Object> getSuccessfulInsertIdAndPictureResponse(Long id, String picture) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "ok");
        response.put("insert_id", id);
        response.put("picture", picture);
        return response;
    }

    public static ResponseEntity buildSuccessfulInsertIdAndPictureResponse(Long id, String picture) {
        return ResponseEntity.status(HttpStatus.OK).cacheControl(getCacheControl()).body(getSuccessfulInsertIdAndPictureResponse(id, picture));
    }

    public static ResponseEntity buildSuccessfulCountingResponse(int contactNumber, int groupNumber) {
        return ResponseEntity.status(HttpStatus.OK).cacheControl(getCacheControl()).body(getSuccessfulCountingResponse(contactNumber, groupNumber));
    }

    public static CacheControl getCacheControl() {
        return CacheControl.noCache().noStore().mustRevalidate().maxAge(Duration.ZERO);
    }

    public static ResponseEntity buildErrorResponse(Exception e) {
        if (e instanceof BadBodyException || e instanceof ImageBadFormatException || e instanceof TokenValidationException
            || e instanceof BadPathVariable) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse(e.getMessage()));
        } else if (e instanceof JsonProcessingException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getErrorResponse("Error procesando el token"));
        } else if (e instanceof TokenAuthException || e instanceof AuthenticationException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getErrorResponse(e.getMessage()));
        } else if (e instanceof ForbiddenAccessException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorResponse(e.getMessage()));
        } else if (e instanceof ImageUploadErrorException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseHelper.getErrorResponse(e.getMessage()));
        } else if (e instanceof NotFoundException || e instanceof NoImageException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseHelper.getErrorResponse(e.getMessage()));
        } else if (e instanceof IncorrectResultSizeDataAccessException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseHelper.getErrorResponse(
            "Error fatal, hay varios usuarios con esas credenciales, cuando deberían ser únicos"));
        } else if (e instanceof ConflictException || e instanceof CollisionException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseHelper.getErrorResponse(e.getMessage()));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseHelper.getErrorResponse("Error desconocido."));
        }
    }
}
