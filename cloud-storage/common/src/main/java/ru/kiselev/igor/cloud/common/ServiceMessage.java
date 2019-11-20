package ru.kiselev.igor.cloud.common;


public class ServiceMessage  extends AbstractMessage {
    private String serviceMessage;

    public String getServiceMessage() {
        return serviceMessage;
    }

    public ServiceMessage(String serviceMessage) {
        this.serviceMessage = serviceMessage;
    }
}
