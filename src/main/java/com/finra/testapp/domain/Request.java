package com.finra.testapp.domain;

import com.google.common.io.ByteSource;
import org.joda.time.LocalDateTime;

import java.util.List;

public class Request extends RequestFields {
    private ByteSource fileBody;

    public Request(String fileName, ByteSource fileBody) {
        this(null, fileName, fileBody);
    }

    public Request(Long id, String fileName, ByteSource fileBody) {
        this(id, fileName, fileBody, (List<MetaDataEntry>) null);
    }

    public Request(Long id, String fileName, ByteSource fileBody, List<MetaDataEntry> metaData) {
        this (id, fileName, LocalDateTime.now(), fileBody, metaData);
    }

    public Request(Long id, String fileName, LocalDateTime asOf, ByteSource fileBody, List<MetaDataEntry> metaData) {
        super(id, fileName, asOf, metaData);
        this.fileBody = fileBody;
    }

    public Long getId() {
        return super.getId();
    }

    public String getFileName() {
        return super.getFileName();
    }

    public LocalDateTime getAsOf() {
        return super.getAsOf();
    }

    public ByteSource getFileBody() {
        return fileBody;
    }

    public List<MetaDataEntry> getMetaData() {
        return super.getMetaData();
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + getId() +
                ", fileName='" + getFileName() + '\'' +
                ", asOf=" + getAsOf() +
                ", fileBody=" + fileBody +
                ", metaData=" + getMetaData() +
                '}';
    }
}
