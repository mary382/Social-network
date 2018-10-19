package com.bootcamp.socialnetwork.web.rest.errors;

public class CustomError {

    private String errorMessage;


    public CustomError(String errorMessage){
        this.errorMessage = errorMessage;
    }


    public String getErrorMessage() {
        return errorMessage;
    }
}
