package com.finra.testapp.init;

import com.finra.testapp.dao.AppDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppInitializerImpl implements AppInitializer {
    @Autowired
    private AppDao appDao;

    @Override
    public void init() {
        appDao.createDb();
    }
}
