package com.finra.testapp.rest;

import com.finra.testapp.dao.AppDao;
import com.finra.testapp.domain.MetaDataEntry;
import com.finra.testapp.domain.Request;
import com.google.common.base.Throwables;
import com.google.common.io.ByteSource;
import org.apache.log4j.Logger;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class FileUploadController {
    private static final Logger LOGGER = Logger.getLogger(FileUploadController.class);

    private static final String DESCRIPTION_PARAM = "description";
    private static final String TYPE_PARAM = "type";

    @Autowired
    private AppDao appDao;

    @PostMapping("/")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile fileBody
            , @RequestParam(value = DESCRIPTION_PARAM, required = false) String description
            , @RequestParam(value = TYPE_PARAM, required = false) String type) {
        try {
            if (fileBody == null || fileBody.getBytes().length == 0) {
                throw new IllegalStateException("File body is empty.");
            }
            List<MetaDataEntry> entries = Lists.newArrayList();
            addMetadata(entries, DESCRIPTION_PARAM, description);
            addMetadata(entries, TYPE_PARAM, type);
            final String fileName = fileBody.getOriginalFilename();
            final byte[] body = fileBody.getBytes();
            Request request = new Request(null, fileName, ByteSource.wrap(body), !entries.isEmpty() ? entries : null);
            appDao.saveRequest(request);
            return ResponseEntity.ok(String.format("File {%s} has been uploaded successfully: {%d} bytes.", fileName, body.length));
        } catch (Exception e) {
            LOGGER.error("", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Throwables.getStackTraceAsString(e));
        }
    }

    private List<MetaDataEntry> addMetadata(List<MetaDataEntry> entries, String key, String value) {
        if (entries == null) {
            throw new IllegalStateException("MetaData collection can not be null.");
        }
        if (!Strings.isNullOrEmpty(value)) {
            entries.add(new MetaDataEntry(key, value));
        }
        return entries;
    }
}
