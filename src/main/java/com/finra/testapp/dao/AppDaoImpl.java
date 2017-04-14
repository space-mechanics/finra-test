package com.finra.testapp.dao;

import com.finra.testapp.domain.Request;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;

@Component
public class AppDaoImpl implements AppDao {
    private final static Logger LOGGER = Logger.getLogger(AppDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void createDb() {
        LOGGER.info("Preparing database ...");
        jdbcTemplate.execute("DROP TABLE REQUEST IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE REQUEST(REQUEST_ID SERIAL, FILE_BODY BLOB, FILE_NAME VARCHAR(255), UPLOAD_DATE DATE)");
    }

    @Override
    public void saveRequest(final Request request) {
        LOGGER.info(String.format("Saving file {%s} as of {%s}.", request.getFileName(), request.getAsOf()));
        jdbcTemplate.update("INSERT INTO REQUEST (FILE_NAME, UPLOAD_DATE, FILE_BODY) VALUES (?, ?, ?)", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                try {
                    ps.setString(1, request.getFileName());
                    ps.setDate(2, new Date(request.getAsOf().toDateTimeAtStartOfDay().getMillis()));
                    ps.setBlob(3, request.getFileBody().openStream());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public List<Request> getAllRequests() {
        List<Request> requests = jdbcTemplate.query("SELECT r.REQUEST_ID, r.FILE_NAME, r.UPLOAD_DATE, r.FILE_BODY FROM REQUEST r",
                new RowMapper<Request>() {
            @Override
            public Request mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                    long id = rs.getLong("REQUEST_ID");
                    String fileName = rs.getString("FILE_NAME");
                    LocalDate asOf = LocalDate.fromDateFields(rs.getDate("UPLOAD_DATE"));
                    Blob fileBody = rs.getBlob("FILE_BODY");
                    byte[] body = ByteStreams.toByteArray(fileBody.getBinaryStream());
                    return new Request(id, fileName, asOf, ByteSource.wrap(body));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return requests;
    }
}
