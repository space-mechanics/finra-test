package com.finra.testapp.util;

import com.finra.testapp.domain.JsonRequest;
import com.finra.testapp.domain.RequestFields;
import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;

public class AppUtils {

    public static JsonRequest toJsonRequest(RequestFields input) {
        LocalDateTime asOf = input.getAsOf();
        String asOfStr = ISODateTimeFormat.dateHourMinuteSecond().print(asOf);
        return new JsonRequest(input.getId(), input.getFileName(), asOfStr, input.getMetaData());
    }
}
