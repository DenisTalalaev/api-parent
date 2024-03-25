package by.salary.authorizationserver.model.oauth2;


import java.util.Map;

public class FactoryAuthenticationRegistration {


    public static AuthenticationUserInfo create(String authenticationRegistrationId, Map<String, Object> attributes){

        if(authenticationRegistrationId.equals(AuthenticationRegistrationId.google.name())){
            return new GoogleAuthentication(attributes);
        }else if(authenticationRegistrationId.equals(AuthenticationRegistrationId.github.name())){
            return new GithubAuthentication(attributes);
        }
        //TODO: add local authentication

        throw new IllegalArgumentException("Unknown authentication registration id: " + authenticationRegistrationId);
    }
}
