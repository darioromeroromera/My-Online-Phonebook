package com.rest.pruebarest.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.rest.pruebarest.exceptions.ForbiddenAccessException;
import com.rest.pruebarest.exceptions.NotFoundException;
import com.rest.pruebarest.helpers.CheckerHelper;
import com.rest.pruebarest.helpers.JWTHelper;
import com.rest.pruebarest.helpers.ResponseHelper;
import com.rest.pruebarest.models.Contact;
import com.rest.pruebarest.models.Message;
import com.rest.pruebarest.models.MessageRequest;
import com.rest.pruebarest.models.MessageResponse;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.ContactRepo;
import com.rest.pruebarest.repos.MessageRepo;
import com.rest.pruebarest.repos.UserRepo;

import io.micrometer.common.lang.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    MessageRepo messageRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ContactRepo contactRepo;

    @GetMapping
    public ResponseEntity getAll(@RequestHeader("Bearer") @Nullable String token) {
        try {
            Long userId = JWTHelper.getUserIdFromToken(token);
            User user = userRepo.findById(userId).get();
            List<Message> messagesReceived = messageRepo.getByDestination(user);
            Collections.reverse(messagesReceived);
            List<Message> messagesSent = messageRepo.getByOrigin(user);
            Collections.reverse(messagesSent);
            return ResponseHelper.buildSuccessfulMessagesResponse(messagesToResponse(messagesReceived, true),
                messagesToResponse(messagesSent, false));
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getOne(@RequestHeader("Bearer") @Nullable String token, @Nullable @PathVariable String id) {
        try {
            CheckerHelper.checkIdFormat(id);
            Long userId = JWTHelper.getUserIdFromToken(token);
            User user = userRepo.findById(userId).get();
            Optional<Message> oMessage = messageRepo.findById(Long.parseLong(id));
            if (oMessage.isEmpty())
                throw new NotFoundException("El mensaje solicitado no existe");
            
            Message message = oMessage.get();
            if (message.getOrigin().getId() != userId && message.getDestination().getId() != userId)
                throw new ForbiddenAccessException("No tienes permisos para acceder a este mensaje");
            if (user == message.getDestination() && !message.isRead())
                markAsRead(message);
            boolean isReceived = message.getDestination().getId() == userId;
            return ResponseHelper.buildSuccessfulDataResponseEntity(singleMessageToResponse(message, isReceived));
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @GetMapping("/check-new")
    public ResponseEntity checkNew(@RequestHeader("Bearer") @Nullable String token) {
        try {
            Long userId = JWTHelper.getUserIdFromToken(token);
            int newMessagesNumber = messageRepo.countNewMessages(userId);
            return ResponseHelper.buildSuccessfulDataResponseEntity(newMessagesNumber);
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @PostMapping
    public ResponseEntity sendMessage(@RequestBody @Nullable MessageRequest messageRequest,
    @RequestHeader("Bearer") @Nullable String token) {
        try {
            Long userId = JWTHelper.getUserIdFromToken(token);
            CheckerHelper.checkMessageParams(messageRequest, userId);
            User user = userRepo.findById(userId).get();
            Message message = new Message();
            message.setOrigin(user);
            message.setDestination(userRepo.findByTelefono(messageRequest.getDestinationPhone()));
            message.setSubject(messageRequest.getSubject());
            message.setText(messageRequest.getText());
            message.setRead(false);
            messageRepo.save(message);
            return ResponseHelper.buildSuccessfulResponseEntity();
        } catch (Exception e) {
            return ResponseHelper.buildErrorResponse(e);
        }
    }

    @RequestMapping
    public ResponseEntity badMethod() {
        return ResponseEntity.badRequest().body(ResponseHelper.getErrorResponse("Verbo HTTP incorrecto"));
    }

    @RequestMapping("/{id}")
    public ResponseEntity badIdMethod() {
        return badMethod();
    }

    public void markAsRead(Message message) {
        message.setRead(true);
        messageRepo.save(message);
    }

    public List<MessageResponse> messagesToResponse(List<Message> messages, boolean isReceived) {
        List<MessageResponse> response = new ArrayList<>();
        for (Message message : messages) {
            MessageResponse singleMessageResponse = new MessageResponse();
            singleMessageResponse.setId(message.getId());
            singleMessageResponse.setOriginPhone(message.getOrigin().getTelefono());
            singleMessageResponse.setDestinationPhone(message.getDestination().getTelefono());
            singleMessageResponse.setSubject(message.getSubject());
            singleMessageResponse.setText(message.getText());
            singleMessageResponse.setRead(message.isRead());
            if (isReceived) {
                Contact contact = contactRepo.findByUserIdAndTelefono(message.getDestination().getId(), singleMessageResponse.getOriginPhone());
                singleMessageResponse.setContactName(contact.getContactName());
            } else {
                Contact contact = contactRepo.findByUserIdAndTelefono(message.getOrigin().getId(), singleMessageResponse.getDestinationPhone());
                singleMessageResponse.setContactName(contact.getContactName());
            }
            response.add(singleMessageResponse);
        }
        return response;
    }

    public MessageResponse singleMessageToResponse(Message message, boolean isReceived) {
        MessageResponse singleMessageResponse = new MessageResponse();
        singleMessageResponse.setId(message.getId());
        singleMessageResponse.setOriginPhone(message.getOrigin().getTelefono());
        singleMessageResponse.setDestinationPhone(message.getDestination().getTelefono());
        singleMessageResponse.setSubject(message.getSubject());
        singleMessageResponse.setText(message.getText());
        singleMessageResponse.setRead(message.isRead());
        if (isReceived) {
            Contact contact = contactRepo.findByUserIdAndTelefono(message.getDestination().getId(), singleMessageResponse.getOriginPhone());
            singleMessageResponse.setContactName(contact.getContactName());
        } else {
            Contact contact = contactRepo.findByUserIdAndTelefono(message.getOrigin().getId(), singleMessageResponse.getDestinationPhone());
            singleMessageResponse.setContactName(contact.getContactName());
        }
        return singleMessageResponse;
    }
}
