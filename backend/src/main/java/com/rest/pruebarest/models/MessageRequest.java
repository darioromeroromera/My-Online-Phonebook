package com.rest.pruebarest.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageRequest {
    private String DestinationPhone;
    String subject;
    String text;
}
