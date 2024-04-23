package by.salary.serviceagreement.filters;


public enum PermissionsEnum {
    ALL_PERMISSIONS,

    //ADMINISTRATOR
    REDACT_ORGANISATION_INFO,
    PROMOTE_USER,

    //MODERATOR
    INVITE_USER,
    EXPIRE_USER,

    CRUD_USER_AGREEMENT,

    CRUD_AGREEMENT_LIST,

    //USER

    READ_ALL_USERS,
    REDACT_USER_INFO,
    AUTHORITY_REDACTION
}
