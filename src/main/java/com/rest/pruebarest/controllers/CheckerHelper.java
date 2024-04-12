package com.rest.pruebarest.controllers;

import com.rest.pruebarest.exceptions.ContactBadBodyException;
import com.rest.pruebarest.exceptions.ContactColisionException;
import com.rest.pruebarest.exceptions.LoginBadBodyException;
import com.rest.pruebarest.exceptions.RegisterBadBodyException;
import com.rest.pruebarest.models.Contact;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.ContactRepo;

public class CheckerHelper {

    private static ContactRepo contactRepo;

    public static void setContactRepo(ContactRepo contactRepo) {
        CheckerHelper.contactRepo = contactRepo;
    }

    public static void checkContactParams(Contact contact) throws ContactBadBodyException {
        if (contact == null) {
            throw new ContactBadBodyException("El body no puede estar vacío");
        }

        if (contact.getId() != null) {
            throw new ContactBadBodyException("El id no puede estar especificado");
        }

        if (contact.getContactName() == null) {
            throw new ContactBadBodyException("El campo contact_name es obligatorio");
        }

        if (contact.getFullName() == null) {
            throw new ContactBadBodyException("El campo full_name es obligatorio");
        }

        if (contact.getTelefono() == null) {
            throw new ContactBadBodyException("El campo telefono es obligatorio");
        }

        if (!contact.getTelefono().matches("[679]\\d{8}"))
            throw new ContactBadBodyException("El teléfono no tiene el formato correcto. Ej: '612345678'");
    }

    public static void checkContactColision(Contact contact) throws ContactColisionException {
        int numberOfUsersByUsername = contactRepo.getByContactNameAndUserId(contact.getUserId(),
                contact.getContactName());

        if (numberOfUsersByUsername != 0)
            throw new ContactColisionException("Ya tienes un contacto con ese nombre, no se pueden repetir");

        int numberOfUsersByPhone = contactRepo.getByTelefonoAndUserId(contact.getUserId(), contact.getTelefono());
        if (numberOfUsersByPhone != 0)
            throw new ContactColisionException("Ya tienes un contacto con ese número, no se pueden repetir");
    }

    public static void checkRegisterParams(User user) throws RegisterBadBodyException {
        if (user == null) {
            throw new RegisterBadBodyException("El body no puede estar vacío");
        }

        if (user.getEmail() == null) {
            throw new RegisterBadBodyException("El email no puede estar vacío");
        }

        if (!user.getEmail().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)?\\.[A-Za-z]{2,6}"))
            throw new RegisterBadBodyException("El email no tiene un formato correcto");

        if (user.getUsername() == null) {
            throw new RegisterBadBodyException("El username no puede estar vacío");
        }

        if (user.getPassword() == null) {
            throw new RegisterBadBodyException("El password no puede estar vacío");
        }

        if (user.getPassword().length() < 8 || !user.getPassword().matches(".*[a-z]+.*")
                || !user.getPassword().matches(".*[A-Z]+.*")
                || !user.getPassword().matches(".*\\d+.*")) {
            throw new RegisterBadBodyException(
                    "El password debe tener al menos 8 caracteres, minúsculas, mayúsculas y números");
        }

        if (user.getToken() != null) {
            throw new RegisterBadBodyException("El token solo puede manipularse cuando se inicia sesión");
        }

        if (user.getId() != null) {
            throw new RegisterBadBodyException("El id no es un parámetro válido");
        }
    }

    public static void checkLoginParams(User user) throws LoginBadBodyException {
        if (user == null) {
            throw new LoginBadBodyException("El body no puede estar vacío");
        }

        if (user.getUsername() == null) {
            throw new LoginBadBodyException("El campo username es obligatorio");
        }

        if (user.getPassword() == null) {
            throw new LoginBadBodyException("El campo password es obligatorio");
        }
    }
}
