package com.finra.testapp.dao;

import com.finra.testapp.domain.FileAttachment;
import com.finra.testapp.domain.Request;
import com.finra.testapp.domain.RequestFields;
import com.google.common.io.ByteSource;

import java.util.List;

public interface AppDao {

    void createDb();

    void saveRequest(Request request);

    FileAttachment getFileById(long id);

    List<RequestFields> getAllRequests();
}
