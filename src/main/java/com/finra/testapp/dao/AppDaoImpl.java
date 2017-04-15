package com.finra.testapp.dao;

import com.finra.testapp.domain.MetaDataEntry;
import com.finra.testapp.domain.Request;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
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

    private static final String SQL_READ_ALL =
            "SELECT " +
                    "r.REQUEST_ID " +
                    ", r.FILE_NAME " +
                    ", r.UPLOAD_TIMESTAMP " +
                    ", r.FILE_BODY " +
                    ", mt.PROPERTY_KEY " +
                    ", mt.PROPERTY_VALUE " +
                    "FROM " +
                    "   REQUEST r " +
                    "   inner join METADATA mt on mt.REQUEST_ID = r.REQUEST_ID " +
                    "order by " +
                    "   r.REQUEST_ID desc";

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
    public List<Request> getAllRequests() {
        final Map<Long, Request> requestMap = Maps.newHashMap();
        jdbcTemplate.query(SQL_READ_ALL,
                new RowMapper<Request>() {
            @Override
            public Request mapRow(ResultSet rs, int rowNum) throws SQLException {
                Request newRequest = readRequestFields(rs);
                Request savedRequest = requestMap.get(newRequest.getId());
                if (savedRequest != null) {
                    ImmutableList.Builder<MetaDataEntry> metadataBuilder = ImmutableList.<MetaDataEntry> builder();
                    for (Request request : new Request[] {newRequest, savedRequest}) {
                        List<MetaDataEntry> metaDataEntries = request.getMetaData();
                        if (metaDataEntries != null) {
                            metadataBuilder.addAll(metaDataEntries);
                        }
                    }
                    newRequest = new Request(newRequest.getId(), newRequest.getFileName(), newRequest.getAsOf(), newRequest.getFileBody(), metadataBuilder.build());
                }
                requestMap.put(newRequest.getId(), newRequest);
                return null;
            }
        });
        return ImmutableList.<Request> builder().addAll(requestMap.values()).build();
    }

    private Request readRequestFields(ResultSet rs) {
        try {
            long id = rs.getLong("REQUEST_ID");
            String fileName = rs.getString("FILE_NAME");
            LocalDateTime asOf = new LocalDateTime(rs.getTimestamp("UPLOAD_TIMESTAMP").getTime());
            Blob fileBody = rs.getBlob("FILE_BODY");
            byte[] body = ByteStreams.toByteArray(fileBody.getBinaryStream());
            String propertyKey = rs.getString("PROPERTY_KEY");
            String propertyValue = rs.getString("PROPERTY_VALUE");
            MetaDataEntry metaDataEntry = null;
            if (!Strings.isNullOrEmpty(propertyKey)) {
                metaDataEntry = new MetaDataEntry(propertyKey, propertyValue);
            }
            return new Request(id, fileName, asOf, ByteSource.wrap(body), metaDataEntry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
