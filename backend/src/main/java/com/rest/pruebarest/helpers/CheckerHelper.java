package com.rest.pruebarest.helpers;

import com.rest.pruebarest.exceptions.ContactColisionException;
import com.rest.pruebarest.exceptions.BadBodyException;
import com.rest.pruebarest.models.ChangePasswordRequest;
import com.rest.pruebarest.models.Contact;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.ContactRepo;

public class CheckerHelper {

    private static ContactRepo contactRepo;

    public static void setContactRepo(ContactRepo contactRepo) {
        CheckerHelper.contactRepo = contactRepo;
    }

    public static void checkContactParams(Contact contact) throws BadBodyException {
        if (contact == null) {
            throw new BadBodyException("El body no puede estar vacío");
        }

        if (contact.getId() != null) {
            throw new BadBodyException("El id no puede estar especificado");
        }

        if (contact.getContactName() == null) {
            throw new BadBodyException("El campo contact_name es obligatorio");
        }

        if (contact.getFullName() == null) {
            throw new BadBodyException("El campo full_name es obligatorio");
        }

        if (contact.getTelefono() == null) {
            throw new BadBodyException("El campo telefono es obligatorio");
        }

        if (!contact.getTelefono().matches("[679]\\d{8}"))
            throw new BadBodyException("El teléfono no tiene el formato correcto. Ej: '612345678'");
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

    public static void checkRegisterParams(User user) throws BadBodyException {
        if (user == null) {
            throw new BadBodyException("El body no puede estar vacío");
        }

        if (user.getEmail() == null) {
            throw new BadBodyException("El email no puede estar vacío");
        }

        if (!user.getEmail().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)?\\.[A-Za-z]{2,6}"))
            throw new BadBodyException("El email no tiene un formato correcto");

        if (user.getUsername() == null) {
            throw new BadBodyException("El username no puede estar vacío");
        }

        if (user.getPassword() == null) {
            throw new BadBodyException("El password no puede estar vacío");
        }

        checkPassword(user.getPassword());

        if (user.getId() != null) {
            throw new BadBodyException("El id no es un parámetro válido");
        }
    }

    public static void checkPassword(String password) throws BadBodyException {
        if (password.length() < 8 || !password.matches(".*[a-z]+.*")
        || !password.matches(".*[A-Z]+.*")
        || !password.matches(".*\\d+.*")) {
            throw new BadBodyException(
                "El password debe tener al menos 8 caracteres, minúsculas, mayúsculas y números");
        }
    }

    public static void checkLoginParams(User user) throws BadBodyException {
        if (user == null) {
            throw new BadBodyException("El body no puede estar vacío");
        }

        if (user.getUsername() == null) {
            throw new BadBodyException("El campo username es obligatorio");
        }

        if (user.getPassword() == null) {
            throw new BadBodyException("El campo password es obligatorio");
        }
    }

    public static void checkProfilePictureParams(User user) throws BadBodyException {
        if (user == null) 
            throw new BadBodyException("El body no puede estar vacío");

        if (user.getProfilePicture() == null)
            throw new BadBodyException("El campo profile_picture es obligatorio");
    }

    public static void checkChangePasswordParams(ChangePasswordRequest request) throws BadBodyException {
        if (request == null)
            throw new BadBodyException("El body no puede estar vacío");
        if (request.getOldPassword() == null)
            throw new BadBodyException("EL campo old_password es obligatorio");
        if (request.getNewPassword() == null)
            throw new BadBodyException("El campo new_password es obligatorio");
    }
}
