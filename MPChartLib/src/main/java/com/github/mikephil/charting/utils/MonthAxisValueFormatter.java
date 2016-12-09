package com.github.mikephil.charting.utils;

import android.util.Log;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.date.ChartDateUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Copyright (C) 2014-2016 daniel@bapul.net
 * Created by Daniel on 2016-12-02.
 */

public class MonthAxisValueFormatter implements IAxisValueFormatter {
	@Override
	public String getFormattedValue(float value, AxisBase axis) {
		return ChartDateUtil.getMonthOfYear(ChartDateUtil.getTimeFromDanielMonthIndex((int) value)) + "월";
	}
}
