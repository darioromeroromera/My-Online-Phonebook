package com.rest.pruebarest.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rest.pruebarest.models.Message;

public interface MessageRepo extends JpaRepository<Message, Long> {
    public abstract List<Message> getByOriginId(Long id);

    public abstract List<Message> getByDestinationId(Long id);
}