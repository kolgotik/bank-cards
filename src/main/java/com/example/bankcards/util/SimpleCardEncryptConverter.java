package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Converts between plain text and encrypted strings using AES-128 encryption.
 * Used as a JPA attribute converter for fields that need to be stored securely.
 */
@Converter
public class SimpleCardEncryptConverter implements AttributeConverter<String, String> {

    //for test only
    private static final String SECRET = "1234567890ABCDEF";
    private static final SecretKeySpec KEY = new SecretKeySpec(SECRET.getBytes(), "AES");

    /**
     * Encrypts a string before persisting it to the database.
     *
     * @param attribute the original (plain text) value
     * @return the Base64-encoded encrypted string
     * @throws RuntimeException if encryption fails
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, KEY);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Encryption error", e);
        }
    }

    /**
     * Decrypts a string retrieved from the database back into plain text.
     *
     * @param dbData the encrypted and Base64-encoded string from the database
     * @return the decrypted plain text value
     * @throws RuntimeException if decryption fails
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, KEY);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new RuntimeException("Decryption error", e);
        }
    }
}
