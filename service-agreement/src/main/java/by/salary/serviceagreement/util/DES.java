package by.salary.serviceagreement.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;

@Component
public class DES {

   private String SECRET;

    public DES(@Value("${database.secret}") String secret) {
        SECRET = secret;
        SecretKeySpec desKey = new SecretKeySpec(
                secret.getBytes(), "AES"
        );
        //des = new DES_Internal(desKey);
    }

    /*public String encrypt(String originalString) {
        return Base64.getEncoder().encodeToString(des.encrypt(originalString.getBytes(StandardCharsets.UTF_8)));
    }

    public String decrypt(String encryptedString) {
        return Arrays.toString(des.decrypt(Base64.getDecoder().decode(encryptedString)));
    }*/

    public String encrypt(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            sb.append((char) (c + 9));
        }
        return sb.toString();
    }


    public String decrypt(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            sb.append((char) (c - 9));
        }
        return sb.toString();
    }
    public static void main(String[] args) {
        String originalString = "Hello, World!";
        DES des  = new DES("123412341234");

        String encryptedString = des.encrypt(originalString);
        String decryptedString = des.decrypt(encryptedString);

        System.out.println("Original: " + originalString);
        System.out.println("Encrypted: " + encryptedString);
        System.out.println("Decrypted: " + decryptedString);
    }
}
