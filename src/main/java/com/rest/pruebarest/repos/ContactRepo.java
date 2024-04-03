package com.rest.pruebarest.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rest.pruebarest.models.Contact;

public interface ContactRepo extends JpaRepository<Contact, Long>{
    
}
