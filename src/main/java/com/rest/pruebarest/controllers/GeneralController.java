package com.rest.pruebarest.controllers;

import java.security.DrbgParameters.Reseed;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneralController {
    /*@GetMapping("error")
    public ResponseEntity error() {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "error");
        response.put("details", "Error desconocido.");
        return ResponseEntity.internalServerError().body(response);
    }*/
}
