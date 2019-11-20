package ru.kiselev.igor.cloud.common;

public class ServiceRequest extends AbstractMessage{
    private String serviceRequest;

    public String getServiceRequest() {
        return serviceRequest;
    }

    public ServiceRequest(String serviceRequest) {
        this.serviceRequest = serviceRequest;
    }
}
