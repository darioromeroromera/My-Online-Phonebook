package com.rest.pruebarest.helpers;

import java.util.HashMap;

public class ResponseHelper {
    public static HashMap<String, Object> getErrorResponse(String details) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "error");
        response.put("details", details);
        return response;
    }
}
