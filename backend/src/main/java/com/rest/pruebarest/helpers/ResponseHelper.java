package com.rest.pruebarest.helpers;

import java.util.HashMap;

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

    public static HashMap<String, Object> getSuccessfulTokenResponse(String token) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "ok");
        response.put("token", token);
        return response;
    }    

    public static HashMap<String, Object> getSuccessfulDataResponse(Object data) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "ok");
        response.put("data", data);
        return response;
    }

    public static HashMap<String, Object> getSuccessfulPictureResponse(String picture) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "ok");
        response.put("picture", picture);
        return response;
    }

    public static HashMap<String, Object> getSuccessfulInsertIdAndPictureResponse(Long id, String picture) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("result", "ok");
        response.put("insert_id", id);
        response.put("picture", picture);
        return response;
    }
}
