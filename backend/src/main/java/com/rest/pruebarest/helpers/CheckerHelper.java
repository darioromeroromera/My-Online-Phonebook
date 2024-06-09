package com.rest.pruebarest.helpers;

import com.rest.pruebarest.exceptions.CollisionException;
import com.rest.pruebarest.exceptions.ForbiddenAccessException;
import com.rest.pruebarest.exceptions.NotFoundException;

import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.rest.pruebarest.exceptions.BadBodyException;
import com.rest.pruebarest.exceptions.BadPathVariable;
import com.rest.pruebarest.models.ChangePasswordRequest;
import com.rest.pruebarest.models.Contact;
import com.rest.pruebarest.models.ContactGroup;
import com.rest.pruebarest.models.Message;
import com.rest.pruebarest.models.MessageRequest;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.ContactRepo;
import com.rest.pruebarest.repos.UserRepo;
import com.rest.pruebarest.repos.ContactGroupRepo;

public class CheckerHelper {

    private static UserRepo userRepo;

    private static ContactRepo contactRepo;

    private static ContactGroupRepo groupRepo;

    public static void setContactRepo(ContactRepo contactRepo) {
        CheckerHelper.contactRepo = contactRepo;
    }

    public static void setGroupRepo(ContactGroupRepo groupRepo) {
        CheckerHelper.groupRepo = groupRepo;
    }

    public static void setUserRepo(UserRepo userRepo) {
        CheckerHelper.userRepo = userRepo;
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

        checkTelefono(contact.getTelefono());
    }

    public static void checkContactColision(Contact contact) throws CollisionException {
        int numberOfUsersByUsername = contactRepo.countByContactNameAndUserId(contact.getUserId(),
            contact.getContactName());

        if (numberOfUsersByUsername != 0)
            throw new CollisionException("Ya tienes un contacto con ese nombre, no se pueden repetir");

        int numberOfUsersByPhone = contactRepo.countByTelefonoAndUserId(contact.getUserId(), contact.getTelefono());
        if (numberOfUsersByPhone != 0)
            throw new CollisionException("Ya tienes un contacto con ese número, no se pueden repetir");
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

        if (user.getTelefono() == null)
        throw new BadBodyException("El teléfono no puede estar vacío");

        checkTelefono(user.getTelefono());

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

    public static void checkTelefono(String telefono) throws BadBodyException {
        if (!telefono.matches("[679]\\d{8}"))
            throw new BadBodyException("El teléfono no tiene el formato correcto. Ej: '612345678'");
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

    public static void checkIdFormat(String id) throws BadPathVariable {
        if (id == null)
            throw new BadPathVariable("No se ha especificado ningún id");

        if (!id.matches("\\d+"))
            throw new BadPathVariable("El id debe ser un número");
    }

    public static void checkGroupParams(ContactGroup group) throws BadBodyException {
        if (group == null)
            throw new BadBodyException("El body no puede estar vacío");

        if (group.getId() != null)
            throw new BadBodyException("El id no puede ser especificado");

        if (group.getName() == null)
            throw new BadBodyException("El campo name es obligatorio");

        if (group.getName().trim().equals(""))
            throw new BadBodyException("El campo name no puede estar vacío");
    }

    public static void checkGroupCollision(ContactGroup group, Long userId) throws CollisionException {
        ContactGroup foundGroup = groupRepo.findByNameAndUserId(group.getName(), userId);

        if (foundGroup != null)
            throw new CollisionException("Ya tienes un grupo con ese nombre, no se pueden repetir");
    }

    public static void checkGroupAuthorization(Long groupId, Long userId) throws NotFoundException, ForbiddenAccessException {
        Optional<ContactGroup> oGroup = groupRepo.findById(groupId);
        if (!oGroup.isPresent())
            throw new NotFoundException("El grupo requerido no existe");

        ContactGroup group = oGroup.get();

        if (group.getUserId() != userId)
            throw new ForbiddenAccessException("El grupo al que se intenta acceder no pertenece a su usuario");
    }

    public static void checkMessageParams(MessageRequest message, Long userId) throws BadBodyException, NotFoundException, ForbiddenAccessException {
        if (message == null)
            throw new BadBodyException("El body no puede estar vacío");

        if (message.getDestinationPhone() == null)
            throw new BadBodyException("El campo destination_phone no puede estar vacío");

        User user = userRepo.findByTelefono(message.getDestinationPhone());

        if (user == null)
            throw new NotFoundException("El destino del mensaje no existe");

        if (user.getId() == userId)
            throw new BadBodyException("No puedes mandarte un mensaje a ti mismo");

        int numberOfMatchingContacts = contactRepo.countByTelefonoAndUserId(userId, user.getTelefono());

        if (numberOfMatchingContacts == 0)
            throw new ForbiddenAccessException("El destino del mensaje no pertenece a tus contactos");
    
        if (message.getSubject() == null)
            throw new BadBodyException("El campo subject no puede estar vacío");

        if (message.getText() == null)
            throw new BadBodyException("El campo text no puede estar vacío");
    }
        
}
