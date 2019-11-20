package ru.kiselev.igor.cloud.common;

public class FileDeleteMessage extends AbstractMessage {

    private String fileDelete;
    private String userFolder;

    public String getFileDeleteName() {
        return fileDelete;
    }

    public String getUserFolder() {
        return userFolder;
    }

    public FileDeleteMessage(String fileDelete, String userFolder) {

        this.fileDelete = fileDelete;
        this.userFolder = userFolder;
    }
}
