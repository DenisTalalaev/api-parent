package by.salary.serviceagreement.util;

import by.salary.serviceagreement.service.DES;
import jakarta.persistence.AttributeConverter;
import lombok.AllArgsConstructor;
import org.apache.hc.client5.http.utils.Base64;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

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
            String encrypted = des.encrypt(attribute);

            String fromBase64 = new String(Base64.encodeBase64(encrypted.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

            return fromBase64;
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
            String base64 = new String(Base64.decodeBase64(dbData.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);


            String decrypted = des.decrypt(base64);

            return decrypted;
        }catch (Exception e){
            throw new RuntimeException("Failed to decrypt attribute" , e);
        }
    }
}
