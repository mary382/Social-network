package com.bootcamp.socialnetwork.util;

/**
 * Class representation for returned object from backend.
 */
public class Response {
    private Object data;
    private String message;
    private String error;

    public Response() {
    }

    public Response(Object data, String message, String error) {
        this.data = data;
        this.message = message;
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
