package by.salary.authorizationserver.service;

import junit.framework.TestCase;
import org.junit.Test;

public class TokenRegistrationServiceTest extends TestCase {

    public void testGenerateVerificationCodeTest(){
        TokenRegistrationService service = new TokenRegistrationService(null, null);

        for (int i = 0; i < 999_999; i++){
            String code = service.generateVerificationCode();
            assertEquals(9, code.length());
        }
    }
}