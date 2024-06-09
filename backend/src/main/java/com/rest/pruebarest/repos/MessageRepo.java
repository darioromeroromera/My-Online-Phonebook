package com.rest.pruebarest.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rest.pruebarest.models.Message;
import com.rest.pruebarest.models.User;

public interface MessageRepo extends JpaRepository<Message, Long> {
    public abstract List<Message> getByOrigin(User origin);

    public abstract List<Message> getByDestination(User destination);
}