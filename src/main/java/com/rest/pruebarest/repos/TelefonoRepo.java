package com.rest.pruebarest.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rest.pruebarest.models.Telefono;

public interface TelefonoRepo extends JpaRepository<Telefono, Long> {
    
}
