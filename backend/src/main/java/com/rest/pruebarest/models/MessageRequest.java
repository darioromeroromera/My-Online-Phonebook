package com.rest.pruebarest.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageRequest {
    private String destinationPhone;
    String subject;
    String text;
}
