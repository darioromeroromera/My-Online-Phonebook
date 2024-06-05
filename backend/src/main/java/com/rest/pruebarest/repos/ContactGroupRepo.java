package com.rest.pruebarest.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rest.pruebarest.models.ContactGroup;

public interface ContactGroupRepo extends JpaRepository<ContactGroup, Long> {
    
    public List<ContactGroup> findByUserIdOrderByName(Long userId);

    public ContactGroup findByNameAndUserId(String name, Long userId);

    @Query(value = "SELECT count(*) FROM contact_group WHERE user_id = ?1", nativeQuery = true)
    public int countGroups(Long userId);
}
