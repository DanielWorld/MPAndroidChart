package com.xxmassdeveloper.mpchartexample.custom;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.xxmassdeveloper.mpchartexample.util.DateUtil;


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


        return DateUtil.getMonthOfYear(currentTime) + "/" + DateUtil.getDayOfMonth(currentTime);
    }
}
