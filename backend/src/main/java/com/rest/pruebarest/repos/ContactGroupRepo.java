package com.rest.pruebarest.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rest.pruebarest.models.ContactGroup;

public interface ContactGroupRepo extends JpaRepository<ContactGroup, Long> {
    
    public List<ContactGroup> findByUserIdOrderByName(Long userId);

    public ContactGroup findByName(String name);
}
