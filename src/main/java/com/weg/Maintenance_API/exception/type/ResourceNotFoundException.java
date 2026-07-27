package com.weg.Maintenance_API.exception.type;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super(resourceName + " não encontrado(a): " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
