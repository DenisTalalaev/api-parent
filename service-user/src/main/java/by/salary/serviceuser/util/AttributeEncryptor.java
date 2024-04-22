package by.salary.serviceuser.util;

import jakarta.persistence.AttributeConverter;
import lombok.AllArgsConstructor;
import org.apache.hc.client5.http.utils.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;

@Component
@AllArgsConstructor
public class AttributeEncryptor implements AttributeConverter<String, String> {

    DES des;


    @Override
    public String convertToDatabaseColumn(String attribute) {

        if (attribute == null){
            return null;
        }
        try {
            return des.encrypt(attribute);
        }catch (Exception e){
            throw new RuntimeException("Failed to encrypt attribute" , e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null){
            return null;
        }

        try {
            return des.decrypt(dbData);
        }catch (Exception e){
            throw new RuntimeException("Failed to decrypt attribute" , e);
        }
    }
}
