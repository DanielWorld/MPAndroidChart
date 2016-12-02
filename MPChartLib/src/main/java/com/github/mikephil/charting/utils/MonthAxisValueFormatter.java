package com.github.mikephil.charting.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Copyright (C) 2014-2016 daniel@bapul.net
 * Created by Daniel on 2016-12-02.
 */

public class MonthAxisValueFormatter implements IAxisValueFormatter {
	@Override
	public String getFormattedValue(float value, AxisBase axis) {
		int month = (int) value % 12;
		return ((month == 0) ? 12 : month) + "월";
	}
}
