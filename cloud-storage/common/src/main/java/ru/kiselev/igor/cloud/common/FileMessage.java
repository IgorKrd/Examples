package ru.kiselev.igor.cloud.common;

import java.io.IOException;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage {
    private String filename;
    private byte[] data;
    private String userFolder;
    private int partsCount;
    private int partNumber;

    public int getPartsCount() {
        return partsCount;
    }
    public int getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }

    public String getUserFolder() {
        return userFolder;
    }

    public FileMessage(Path path, String userFolder, byte[] data, int partsCount, int partNumber) throws IOException {
        filename = path.getFileName().toString();
        this.userFolder = userFolder;
        this.partNumber = partNumber;
        this.partsCount = partsCount;
        this.data = data;
    }

}
