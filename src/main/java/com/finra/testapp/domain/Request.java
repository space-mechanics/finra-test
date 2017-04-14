package com.finra.testapp.domain;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;
import org.joda.time.LocalDateTime;

import java.util.List;

public class Request {
    private Long id;
    private String fileName;
    private LocalDateTime asOf;
    private ByteSource fileBody;
    private List<MetaDataEntry> metaData;

    public Request(String fileName, ByteSource fileBody) {
        this(null, fileName, LocalDateTime.now(), fileBody);
    }

    public Request(Long id, String fileName, LocalDateTime asOf, ByteSource fileBody) {
        this(id, fileName, asOf, fileBody, null);
    }

    public Request(Long id, String fileName, LocalDateTime asOf, ByteSource fileBody, List<MetaDataEntry> metaData) {
        this.id = id;
        this.fileName = fileName;
        this.asOf = asOf;
        this.fileBody = fileBody;
        if (metaData != null) {
            this.metaData = ImmutableList.copyOf(metaData);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public LocalDateTime getAsOf() {
        return asOf;
    }

    public ByteSource getFileBody() {
        return fileBody;
    }

    public List<MetaDataEntry> getMetaData() {
        return metaData;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", asOf=" + asOf +
                ", fileBody=" + fileBody +
                ", metaData=" + metaData +
                '}';
    }
}
