package com.finra.testapp.domain;

import com.google.common.collect.ImmutableList;
import org.joda.time.LocalDateTime;

import java.util.List;

public class RequestFields {
    private Long id;
    private String fileName;
    private LocalDateTime asOf;
    private List<MetaDataEntry> metaData;

    public RequestFields(Long id, String fileName, LocalDateTime asOf, List<MetaDataEntry> metaData) {
        this.id = id;
        this.fileName = fileName;
        this.asOf = asOf;
        if (metaData != null) {
            this.metaData = ImmutableList.copyOf(metaData);
        }
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public LocalDateTime getAsOf() {
        return asOf;
    }

    public List<MetaDataEntry> getMetaData() {
        return metaData;
    }
}
