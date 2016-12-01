package com.xxmassdeveloper.mpchartexample.custom;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Copyright (c) 2014-2016 op7773hons@gmail.com
 * Created by Daniel Park on 2016-11-27.
 */

public class MonthAxisValueFormatter implements IAxisValueFormatter{

//    private BarLineChartBase<?> chart;

    public MonthAxisValueFormatter(BarLineChartBase<?> chart) {
//        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int month = (int) value % 12;
        return ((month == 0) ? 12 : month) + "월";
    }
}
