package com.finra.testapp.domain;

import com.google.common.io.ByteSource;
import org.joda.time.LocalDate;

public class Request {
    private Long id;
    private String fileName;
    private LocalDate asOf;
    private ByteSource fileBody;

    public Request(String fileName, ByteSource fileBody) {
        this(null, fileName, LocalDate.now(), fileBody);
    }

    public Request(Long id, String fileName, LocalDate asOf, ByteSource fileBody) {
        this.id = id;
        this.fileName = fileName;
        this.asOf = asOf;
        this.fileBody = fileBody;
    }

    public String getFileName() {
        return fileName;
    }

    public LocalDate getAsOf() {
        return asOf;
    }

    public ByteSource getFileBody() {
        return fileBody;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", asOf=" + asOf +
                '}';
    }
}
