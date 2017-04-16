package com.finra.testapp.dao.rowmapper;

import com.finra.testapp.domain.Builder;
import com.finra.testapp.domain.MetaDataEntry;
import com.finra.testapp.domain.RequestFields;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.assertj.core.util.Strings;
import org.joda.time.LocalDateTime;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FileMetaDataRowMapper implements RowMapper<RequestFields>, Builder<List<RequestFields>> {
    final Map<Long, RequestFields> requestMap = Maps.newHashMap();

    @Override
    public RequestFields mapRow(ResultSet rs, int rowNum) throws SQLException {
        RequestFields newRequest = readRequestFields(rs);
        RequestFields savedRequest = requestMap.get(newRequest.getId());
        if (savedRequest != null) {
            ImmutableList.Builder<MetaDataEntry> metadataBuilder = ImmutableList.<MetaDataEntry> builder();
            for (RequestFields request : new RequestFields[] {newRequest, savedRequest}) {
                List<MetaDataEntry> metaDataEntries = request.getMetaData();
                if (metaDataEntries != null) {
                    metadataBuilder.addAll(metaDataEntries);
                }
            }
            newRequest = new RequestFields(newRequest.getId(), newRequest.getFileName(), newRequest.getAsOf(), metadataBuilder.build());
        }
        requestMap.put(newRequest.getId(), newRequest);
        return null;
    }

    private RequestFields readRequestFields(ResultSet rs) {
        try {
            long id = rs.getLong("REQUEST_ID");
            String fileName = rs.getString("FILE_NAME");
            LocalDateTime asOf = new LocalDateTime(rs.getTimestamp("UPLOAD_TIMESTAMP").getTime());
            String propertyKey = rs.getString("PROPERTY_KEY");
            String propertyValue = rs.getString("PROPERTY_VALUE");
            MetaDataEntry metaDataEntry = null;
            if (!Strings.isNullOrEmpty(propertyKey)) {
                metaDataEntry = new MetaDataEntry(propertyKey, propertyValue);
            }
            return new RequestFields(id, fileName, asOf, Arrays.asList(metaDataEntry));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RequestFields> build() {
        return ImmutableList.<RequestFields> builder().addAll(requestMap.values()).build();
    }
}
