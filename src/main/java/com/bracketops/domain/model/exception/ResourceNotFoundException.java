package com.bracketops.domain.model.exception;

public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(resourceName + " with identifier '" + identifier + "' was not found.");
    }
}
