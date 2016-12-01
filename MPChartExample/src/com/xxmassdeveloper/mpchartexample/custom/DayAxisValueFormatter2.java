package com.xxmassdeveloper.mpchartexample.custom;

import com.github.mikephil.charting.DANIEL;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;

import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * Copyright (c) 2014-2016 op7773hons@gmail.com
 * Created by Daniel Park on 2016-11-26.
 */

public class DayAxisValueFormatter2 implements IAxisValueFormatter
{

//    private BarLineChartBase<?> chart;

    public DayAxisValueFormatter2(BarLineChartBase<?> chart) {
//        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        long currentTime = (long) (value * (1000 * 60 * 60 * 24));

        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(currentTime);
        DateTime jodaTime = new DateTime(currentTime,
                DateTimeZone.forTimeZone(TimeZone.getDefault()));

//        DANIEL.log().d("year "+jodaTime.get(DateTimeFieldType.year()));
//        DANIEL.log().d("month "+jodaTime.get(DateTimeFieldType.monthOfYear()));
//        DANIEL.log().d("day "+jodaTime.get(DateTimeFieldType.dayOfMonth()));

        return jodaTime.get(DateTimeFieldType.monthOfYear()) + "/" + jodaTime.get(DateTimeFieldType.dayOfMonth());
    }
}
