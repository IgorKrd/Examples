package ru.kiselev.igor.cloud.common;

import java.util.ArrayList;

public class RefreshServerStorageMessage extends AbstractMessage {

    private ArrayList<String> findFiles;

    public ArrayList<String> getFindFiles() {
        return findFiles;
    }

    public RefreshServerStorageMessage(ArrayList<String> findFiles) {
        this.findFiles = findFiles;
    }
}
