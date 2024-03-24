package by.salary.authorizationserver.model.oauth2;

public interface AuthenticationUserInfo {


    String getId();

    String getEmail();

    String getUri();

    String getNameAttribureKey();

    AuthenticationRegistrationId getAuthenticationAttributeKey();


}
