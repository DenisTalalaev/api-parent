package by.salary.apigateway.model;

import lombok.AllArgsConstructor;

import java.util.Date;

@AllArgsConstructor
public class ExceptionResponseModel {

    private String errorCode;
    private String message;

    private String details;

    private String help;
    private Date timestamp;



}
