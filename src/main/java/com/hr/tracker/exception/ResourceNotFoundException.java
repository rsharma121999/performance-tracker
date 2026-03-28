package com.hr.tracker.exception;


public class ResourceNotFoundException extends RuntimeException {

    private final String resource;
    private final Long id;

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
        this.resource = resource;
        this.id = id;
    }

    public String getResource() { return resource; }
    public Long getResourceId() { return id; }
}
