package com.finra.testapp.domain;

public class FileAttachment {
    private String fileName;
    private byte[] fileBody;

    public FileAttachment(String fileName, byte[] fileBody) {
        this.fileName = fileName;
        this.fileBody = fileBody;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileBody() {
        return fileBody;
    }
}
