package by.salary.serviceagreement.util;

import by.salary.serviceagreement.service.DES;
import jakarta.persistence.AttributeConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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
