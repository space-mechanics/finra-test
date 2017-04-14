package com.finra.testapp.test;

import com.finra.testapp.SpringConfig;
import com.finra.testapp.dao.AppDao;
import com.finra.testapp.domain.Request;
import com.google.common.io.ByteSource;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringConfig.class})
public class DaoTestIT {
    private final static Logger LOGGER = Logger.getLogger(DaoTestIT.class);

    @Autowired
    private AppDao appDao;

    @Test
    public void testInsertRequest() {
        Request request = new Request("test_file.txt", ByteSource.wrap("test".getBytes()));
        appDao.saveRequest(request);
    }

    @Test
    public void testWriteRead() {
        for (int i = 1; i < 11; i++) {
            write(i);
        }
        List<Request> requests = appDao.getAllRequests();
        for (Request request : requests) {
            LOGGER.info(request);
        }
    }

    private void write(int iteration) {
        String fileName = String.format("file_%d", iteration);
        String body = String.format("test_%d", iteration);
        Request request = new Request(fileName, ByteSource.wrap(body.getBytes()));
        appDao.saveRequest(request);

    }
}
