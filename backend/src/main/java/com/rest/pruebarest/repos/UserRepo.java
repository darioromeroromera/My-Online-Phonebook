package com.rest.pruebarest.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rest.pruebarest.models.User;

public interface UserRepo extends JpaRepository<User, Long> {
    public abstract User findByUsername(String username);

    public abstract User findByEmail(String email);

    public abstract User findByTelefono(String telefono);
}
