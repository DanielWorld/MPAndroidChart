package com.xxmassdeveloper.mpchartexample.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;

import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Copyright (C) 2014-2016 daniel@bapul.net
 * Created by Daniel on 2016-12-01.
 */

public class DateUtil {

    public static int getCurrentMonthOfYear() {
        long currentTime = System.currentTimeMillis();

        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(currentTime);
        DateTime jodaTime = new DateTime(currentTime,
                DateTimeZone.forTimeZone(TimeZone.getDefault()));

//        DANIEL.log().d("year "+jodaTime.get(DateTimeFieldType.year()));
//        DANIEL.log().d("month "+jodaTime.get(DateTimeFieldType.monthOfYear()));
//        DANIEL.log().d("day "+jodaTime.get(DateTimeFieldType.dayOfMonth()));

        return jodaTime.get(DateTimeFieldType.monthOfYear());
    }
}
