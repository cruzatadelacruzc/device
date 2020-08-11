package com.example.devices.web.rest.errors;

import lombok.Data;

import java.io.Serializable;

@Data
public class FieldErrorMV implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String objectName;

    private final String field;

    private final String message;
}
