package com.rest.pruebarest.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.rest.pruebarest.helpers.CheckerHelper;
import com.rest.pruebarest.helpers.ImageHelper;
import com.rest.pruebarest.helpers.JWTHelper;
import com.rest.pruebarest.helpers.ResponseHelper;
import com.rest.pruebarest.models.Contact;
import com.rest.pruebarest.models.Message;
import com.rest.pruebarest.repos.MessageRepo;

import io.micrometer.common.lang.Nullable;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    MessageRepo messageRepo;

    @GetMapping
    public ResponseEntity getAll(@RequestHeader("Bearer") @Nullable String token) {
        try {
            Long userId = JWTHelper.getUserIdFromToken(token);
            List<Message> messagesReceived = messageRepo.getByDestinationId(userId);
            List<Message> messagesSent = messageRepo.getByOriginId(userId);
            return ResponseHelper.buildSuccessfulMessagesResponse(messagesReceived, messagesSent);
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

        @PostMapping
    public ResponseEntity sendMessage(@RequestBody @Nullable Message message,
    @RequestHeader("Bearer") @Nullable String token) {
        try {
            CheckerHelper.checkMessageParams(message);
            Long userId = JWTHelper.getUserIdFromToken(token);
            message.setOriginId(userId);
            message.setRead(false);
            messageRepo.save(message);
            return ResponseHelper.buildSuccessfulResponseEntity();
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }
}
