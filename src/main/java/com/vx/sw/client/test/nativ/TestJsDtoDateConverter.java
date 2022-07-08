package com.vx.sw.client.test.nativ;

import java.util.Date;

import com.google.gwt.core.client.JsDate;
import com.vx.sw.client.common.Empty;
import com.vx.sw.client.test.nativ.TestDtoUtils.DtoDateConverter;

public class TestJsDtoDateConverter implements DtoDateConverter {

    @Override
    public TestDtoDate fromDate(Date value) {
        if (value == null) return null;
        JsDate jsd = JsDate.create(value.getTime());
        return TestDtoDate.create(jsd.getTime(), jsd.toUTCString());
    }

    @Override
    public Date toDate(TestDtoDate value) {
        if (value == null) return null;
        if (value.msec != null) return TestDtoUtils.numToDate(value.msec);
        else if (!Empty.is(value.utc)) {
            JsDate jsd = JsDate.create(value.utc);
            return TestDtoUtils.numToDate(jsd.getTime());
        } else {
            return null;
        }
    }

}
