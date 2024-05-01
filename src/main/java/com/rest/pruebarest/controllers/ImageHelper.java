package com.rest.pruebarest.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rest.pruebarest.exceptions.ImageBadFormatException;
import com.rest.pruebarest.exceptions.ImageUploadErrorException;
import com.rest.pruebarest.exceptions.NoImageException;
import com.rest.pruebarest.models.Contact;
import com.rest.pruebarest.models.User;
import com.rest.pruebarest.repos.ContactRepo;
import com.rest.pruebarest.repos.UserRepo;

public class ImageHelper {

    private static ContactRepo contactRepo;

    private static UserRepo userRepo;

    public static void setContactRepo(ContactRepo contactRepo) {
        ImageHelper.contactRepo = contactRepo;
    }

    public static void setUserRepo(UserRepo userRepo) {
        ImageHelper.userRepo = userRepo;
    }

    public static String generateFilename(String extension) {
        String id = UUID.randomUUID().toString().replace("-", "");
        return new StringBuilder(id).append('.').append(extension).toString();
    }

    public static void changeContactPicture(Contact contact) throws ImageBadFormatException, ImageUploadErrorException {
        String[] tokens = contact.getContactPicture().split(",");
        if (tokens.length != 2) {
            throw new ImageBadFormatException("Los datos de la imagen deben llevar solo una coma obligatoriamente");
        }

        Pattern patron = Pattern.compile("data:image/(jpeg|png);base64");

        Matcher matcher = patron.matcher(tokens[0].trim());

        if (!matcher.find()) {
            throw new ImageBadFormatException("La imagen debe ser formato jpeg o png");
        }

        byte[] imgBytes = Base64.getDecoder().decode(tokens[1]);

        String filename = generateFilename(matcher.group(1));

        try {
            String route = "src/main/resources/static/" + filename;
            Files.write(Paths.get(route), imgBytes);
            contact.setContactPicture("http://localhost:8080/" + filename);
        } catch (IOException e) {
            throw new ImageUploadErrorException("Ha ocurrido un error intentando subir la imagen");
        }
    }

    public static void changeProfilePicture(User user) throws ImageBadFormatException, ImageUploadErrorException {
        String[] tokens = user.getProfilePicture().split(",");
        if (tokens.length != 2) {
            throw new ImageBadFormatException("Los datos de la imagen deben llevar solo una coma obligatoriamente");
        }

        Pattern patron = Pattern.compile("data:image/(jpeg|png);base64");

        Matcher matcher = patron.matcher(tokens[0].trim());

        if (!matcher.find()) {
            throw new ImageBadFormatException("La imagen debe ser formato jpeg o png");
        }

        byte[] imgBytes = Base64.getDecoder().decode(tokens[1]);

        String filename = generateFilename(matcher.group(1));

        try {
            String route = "src/main/resources/static/" + filename;
            Files.write(Paths.get(route), imgBytes);
            user.setProfilePicture("http://localhost:8080/" + filename);
        } catch (IOException e) {
            throw new ImageUploadErrorException("Ha ocurrido un error intentando subir la imagen");
        }
    }

    public static void deleteContactPicture(Contact contact) throws NoImageException, ImageBadFormatException {
        if (contact.getContactPicture() == null) {
            throw new NoImageException("El contacto no tiene una imagen para poder borrarla");
        }

        Pattern pattern = Pattern.compile("http://localhost:8080/(.+)");

        Matcher matcher = pattern.matcher(contact.getContactPicture());

        if (!matcher.matches()) {
            throw new ImageBadFormatException(
                    "El campo contact_picture del contacto no tiene el formato correcto. Poniendo el campo a vacío");
        }

        contact.setContactPicture(null);
        contactRepo.save(contact);

        String route = "src/main/resources/static/" + matcher.group(1);

        File imgFile = new File(route);

        if (imgFile.exists()) {
            imgFile.delete();
        } else {
            throw new NoImageException("La imagen que tenía asociada el contacto no existe. Poniendo el campo vacío.");
        }
    }

    public static void deleteProfilePicture(User user) throws NoImageException, ImageBadFormatException {
        if (user.getProfilePicture() == null) {
            throw new NoImageException("El usuario no tiene una imagen para poder borrarla");
        }

        Pattern pattern = Pattern.compile("http://localhost:8080/(.+)");

        Matcher matcher = pattern.matcher(user.getProfilePicture());

        if (!matcher.matches()) {
            throw new ImageBadFormatException(
                    "El campo profile_picture del usuario no tiene el formato correcto. Poniendo el campo a vacío");
        }

        user.setProfilePicture(null);
        userRepo.save(user);

        String route = "src/main/resources/static/" + matcher.group(1);

        File imgFile = new File(route);

        if (imgFile.exists()) {
            imgFile.delete();
        } else {
            throw new NoImageException("La imagen que tenía asociada el contacto no existe. Poniendo el campo vacío.");
        }
    }
}
