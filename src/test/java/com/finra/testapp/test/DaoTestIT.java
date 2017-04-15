package com.finra.testapp.test;

import com.finra.testapp.SpringConfig;
import com.finra.testapp.dao.AppDao;
import com.finra.testapp.domain.MetaDataEntry;
import com.finra.testapp.domain.Request;
import com.google.common.io.ByteSource;
import org.apache.log4j.Logger;
import org.assertj.core.util.Lists;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringConfig.class})
public class DaoTestIT {
    private static final Logger LOGGER = Logger.getLogger(DaoTestIT.class);

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
        List<MetaDataEntry> metaDataEntries = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            MetaDataEntry entry = new MetaDataEntry(String.format("Key_%d_%d", iteration, i), String.format("Value_%d_%d", iteration, i));
            metaDataEntries.add(entry);
        }
        Request request = new Request(null, fileName, LocalDateTime.now(), ByteSource.wrap(body.getBytes()), metaDataEntries);
        appDao.saveRequest(request);
    }
}
