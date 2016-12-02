package com.xxmassdeveloper.mpchartexample.util;

import com.github.mikephil.charting.DANIEL;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Copyright (C) 2014-2016 daniel@bapul.net
 * Created by Daniel on 2016-12-01.
 */

public class DateUtil {

    /**
     * 현재 ?월 인지 알려줌
     * @return
     */
    public static int getCurrentMonthOfYear() {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Daniel (2016-12-02 12:08:44): Month 의 경우 Month = 0가 1월이다.
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * yyyy 년 MM 월 dd 일 을 milliseconds 로 바꿔줌
     * @return
     */
    public static long getMillisFromDate(int year, int month, int day) {
        // Daniel (2016-12-02 12:08:44): Month 의 경우 Month = 0가 1월이다.
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.set(year, month - 1, day);

        return calendar.getTimeInMillis();
    }

    /**
     * 해당 millis 의 month 가져오기
     * @param milliseconds
     * @return
     */
    public static int getMonthOfYear(long milliseconds) {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(milliseconds);
        // Daniel (2016-12-02 12:08:44): Month 의 경우 Month = 0가 1월이다.
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 해당 millis 의 day 가져오기
     * @param milliseconds
     * @return
     */
    public static int getDayOfMonth(long milliseconds) {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(milliseconds);
        // Daniel (2016-12-02 12:08:44): Month 의 경우 Month = 0가 1월이다.
        return calendar.get(Calendar.MONTH) + 1;
    }

}
