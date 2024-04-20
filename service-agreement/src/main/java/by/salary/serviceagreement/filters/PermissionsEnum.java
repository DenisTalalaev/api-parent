package by.salary.serviceagreement.filters;

public enum PermissionsEnum {
    ALL_PERMISSIONS,

    //ADMINISTRATOR
    REDACT_ORGANISATION_INFO,
    GRANT_ORGANISATION,
    PROMOTE_USER,
    DEMOTE_USER,

    //MODERATOR
    INVITE_USER,
    EXPIRE_USER,

    CRUD_USER_AGREEMENT,

    CRUD_AGREEMENT_LIST,

    CRUD_AGREEMENT_STATE,
    //USER

    READ_ALL_USERS,
    AUTHORITY_REDACTION

}
