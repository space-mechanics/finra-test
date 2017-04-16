package com.finra.testapp.domain;

import java.util.List;

public class JsonRequest {
    private Long id;
    private String fileName;
    private String asOf;
    private List<MetaDataEntry> metaData;

    public JsonRequest(Long id, String fileName, String asOf, List<MetaDataEntry> metaData) {
        this.id = id;
        this.fileName = fileName;
        this.asOf = asOf;
        this.metaData = metaData;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAsOf() {
        return asOf;
    }

    public List<MetaDataEntry> getMetaData() {
        return metaData;
    }
}
