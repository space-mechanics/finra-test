package com.finra.testapp.dao;

import com.finra.testapp.domain.Request;

import java.util.List;

public interface AppDao {

    void createDb();

    void saveRequest(Request request);

    List<Request> getAllRequests();
}
