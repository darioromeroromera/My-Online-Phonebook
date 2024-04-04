package com.rest.pruebarest.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rest.pruebarest.models.Contact;

public interface ContactRepo extends JpaRepository<Contact, Long> {
    public abstract List<Contact> findByUserId(Long id);
}
