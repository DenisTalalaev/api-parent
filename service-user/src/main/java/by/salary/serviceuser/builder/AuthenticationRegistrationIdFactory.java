package by.salary.serviceuser.builder;

import by.salary.serviceuser.interfaces.AuthenticationRegistrationId;

public class AuthenticationRegistrationIdFactory {

    public static AuthenticationRegistrationId create(String name){
        return AuthenticationRegistrationId.valueOf(name);
    }

}
