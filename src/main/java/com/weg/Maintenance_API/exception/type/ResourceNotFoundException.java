package com.weg.Maintenance_API.exception.type;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super(resourceName + " nÃ£o encontrado(a): " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
