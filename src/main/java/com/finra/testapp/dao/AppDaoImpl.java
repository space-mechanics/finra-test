package com.finra.testapp.dao;

import com.finra.testapp.dao.rowmapper.FileMetaDataRowMapper;
import com.finra.testapp.domain.FileAttachment;
import com.finra.testapp.domain.MetaDataEntry;
import com.finra.testapp.domain.Request;
import com.finra.testapp.domain.RequestFields;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import org.apache.log4j.Logger;
import org.assertj.core.util.Strings;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class AppDaoImpl implements AppDao {
    private static final Logger LOGGER = Logger.getLogger(AppDaoImpl.class);

    private static final String SQL_CREATE_REQUEST_TABLE =
            "CREATE TABLE REQUEST(REQUEST_ID BIGINT auto_increment primary key, FILE_BODY BLOB, FILE_NAME VARCHAR(255), UPLOAD_TIMESTAMP TIMESTAMP)";

    private static final String SQL_CREATE_METADATA_TABLE =
            "CREATE TABLE METADATA(METADATA_ID BIGINT auto_increment primary key, REQUEST_ID BIGINT, PROPERTY_KEY VARCHAR(255), PROPERTY_VALUE VARCHAR(255), FOREIGN KEY (REQUEST_ID) REFERENCES REQUEST)";

    private static final String SQL_INSERT_NEW_REQUEST = "INSERT INTO REQUEST (FILE_NAME, UPLOAD_TIMESTAMP, FILE_BODY) VALUES (?, ?, ?)";

    private static final String SQL_INSERT_NEW_METADATA = "INSERT INTO METADATA (REQUEST_ID, PROPERTY_KEY, PROPERTY_VALUE) VALUES (?, ?, ?)";

    private static final String SQL_FILE_FIELDS =
            "SELECT " +
                    "r.REQUEST_ID " +
                    ", r.FILE_NAME " +
                    ", r.UPLOAD_TIMESTAMP " +
                    ", mt.PROPERTY_KEY " +
                    ", mt.PROPERTY_VALUE " +
                    "FROM " +
                    "   REQUEST r " +
                    "   left join METADATA mt on mt.REQUEST_ID = r.REQUEST_ID ";

    private static final String SQL_FILE_METADATA_BY_ID =
            SQL_FILE_FIELDS +
                    "WHERE " +
                    "   r.REQUEST_ID = ?";

    private static final String SQL_READ_ALL =
            SQL_FILE_FIELDS +
                    "order by " +
                    "   r.REQUEST_ID desc";

    private static final String SQL_GET_BODY_BY_REQUEST_ID =
            "SELECT " +
                    "r.FILE_NAME " +
                    ", r.FILE_BODY " +
                    "FROM " +
                    "   REQUEST r " +
                    "WHERE r.REQUEST_ID = ? ";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createDb() {
        LOGGER.info("Preparing database ...");
        jdbcTemplate.execute("DROP TABLE REQUEST IF EXISTS");
        jdbcTemplate.execute(SQL_CREATE_REQUEST_TABLE);
        jdbcTemplate.execute(SQL_CREATE_METADATA_TABLE);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void saveRequest(final Request request) {
        LOGGER.info(String.format("Saving file {%s} as of {%s}.", request.getFileName(), request.getAsOf()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                try {
                    PreparedStatement ps = con.prepareStatement(SQL_INSERT_NEW_REQUEST, new String[] {"REQUEST_ID"});
                    ps.setString(1, request.getFileName());
                    ps.setTimestamp(2, new Timestamp(request.getAsOf().toDate().getTime()));
                    ps.setBlob(3, request.getFileBody().openStream());
                    return ps;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, keyHolder);
        final long requestId = (Long) keyHolder.getKey();
        final List<MetaDataEntry> metaDataEntries = request.getMetaData();
        if (metaDataEntries != null && !metaDataEntries.isEmpty()) {
            jdbcTemplate.batchUpdate(SQL_INSERT_NEW_METADATA, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    MetaDataEntry entry = metaDataEntries.get(i);
                    ps.setLong(1, requestId);
                    ps.setString(2, entry.getKey());
                    ps.setString(3, entry.getValue());
                }

                @Override
                public int getBatchSize() {
                    return metaDataEntries.size();
                }
            });
        }
    }

    @Override
    public RequestFields getFileMetadataById(long id) {
        FileMetaDataRowMapper rowMapper = new FileMetaDataRowMapper();
        jdbcTemplate.query(SQL_FILE_METADATA_BY_ID, rowMapper, id);
        List<RequestFields> metadata = rowMapper.build();
        return Iterables.getFirst(metadata, null);
    }

    @Override
    public List<RequestFields> getAllRequests() {
        FileMetaDataRowMapper rowMapper = new FileMetaDataRowMapper();
        jdbcTemplate.query(SQL_READ_ALL, rowMapper);
        return rowMapper.build();
    }

    @Override
    public FileAttachment getFileById(long id) {
        List<FileAttachment> body = jdbcTemplate.query(SQL_GET_BODY_BY_REQUEST_ID, new RowMapper<FileAttachment>() {
            @Override
            public FileAttachment mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                    String fileName = rs.getString("FILE_NAME");
                    Blob fileBody = rs.getBlob("FILE_BODY");
                    byte[] body = ByteStreams.toByteArray(fileBody.getBinaryStream());
                    return new FileAttachment(fileName, body);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, id);
        return Iterables.getFirst(body, null);
    }
}
