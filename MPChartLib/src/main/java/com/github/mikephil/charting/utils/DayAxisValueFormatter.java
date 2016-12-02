package com.github.mikephil.charting.utils;

import com.github.mikephil.charting.DANIEL;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.date.ChartDateUtil;

/**
 * Copyright (C) 2014-2016 daniel@bapul.net
 * Created by Daniel on 2016-12-02.
 */

public class DayAxisValueFormatter implements IAxisValueFormatter {
	@Override
	public String getFormattedValue(float value, AxisBase axis) {
		long currentTime = (long) (value * (1000 * 60 * 60 * 24));

		return ChartDateUtil.getMonthOfYear(currentTime) + "/" + ChartDateUtil.getDayOfMonth(currentTime);
	}
}
