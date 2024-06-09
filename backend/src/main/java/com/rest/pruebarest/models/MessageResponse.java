package com.rest.pruebarest.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageResponse {
    Long id;

    String originPhone;

    String destinationPhone;

    String contactName;

    String subject;

    String text;

    boolean isRead;
}
