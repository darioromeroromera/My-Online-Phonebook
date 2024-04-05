package com.rest.pruebarest.controllers;

import java.util.UUID;

public class ImageHelper {
    public static String generateFilename(String extension) {
        String id = UUID.randomUUID().toString().replace("-", "");
        return new StringBuilder(id).append('.').append(extension).toString();
    }
}
