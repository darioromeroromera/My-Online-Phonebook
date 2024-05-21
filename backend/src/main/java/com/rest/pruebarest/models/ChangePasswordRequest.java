package com.rest.pruebarest.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordRequest {
    String oldPassword;
    String newPassword;    
}
