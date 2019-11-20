package ru.kiselev.igor.cloud.common;

public class FileRequest extends AbstractMessage {
    private String filename;
    private String userFolder;

    public String getFilename() {
        return filename;
    }

    public String getUserFolder() {
        return userFolder;
    }

    public FileRequest(String userFolder, String filename) {
        this.userFolder = userFolder;
        this.filename = filename;
    }
}
