package com.altas.gateway.schema;

import com.altas.core.annotation.restful.Schema;

@Schema
public class ApiResponse {

    private int code;
    private String message;
    private Object payload;

    public ApiResponse(int error) {
        this.code = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
