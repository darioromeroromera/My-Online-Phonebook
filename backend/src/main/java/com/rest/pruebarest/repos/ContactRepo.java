package com.rest.pruebarest.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rest.pruebarest.models.Contact;

public interface ContactRepo extends JpaRepository<Contact, Long> {
    public abstract List<Contact> findByUserId(Long id);

    @Query(value = "SELECT count(*) FROM contact WHERE user_id = ?1 AND contact_name = ?2", nativeQuery = true)
    public int getByContactNameAndUserId(Long userId, String contactName);

    @Query(value = "SELECT count(*) FROM contact WHERE user_id = ?1 AND telefono = ?2", nativeQuery = true)
    public int getByTelefonoAndUserId(Long userId, String telefono);

    public List<Contact> findByUserIdOrderByContactName(Long userId);

    public List<Contact> findByGroupId(Long groupId);
}
