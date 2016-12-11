package com.xxmassdeveloper.mpchartexample;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.DANIEL;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.xxmassdeveloper.mpchartexample.custom.DayAxisValueFormatter2;
import com.xxmassdeveloper.mpchartexample.custom.MonthAxisValueFormatter;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;
import com.xxmassdeveloper.mpchartexample.util.DateUtil;

import java.util.ArrayList;
import java.util.Random;

/**
 * Copyright (c) 2014-2016 op7773hons@gmail.com
 * Created by Daniel Park on 2016-11-27.
 */

public class BarChartActivityMultiDataset2 extends DemoBase implements
        OnChartValueSelectedListener, View.OnClickListener {

    enum GraphType {
        Days, Months
    }
    GraphType mCurrentGraphType = GraphType.Days;

    private BarChart mChart;
    private TextView weekBtn, monthBtn, addDataBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart2);

        weekBtn = (TextView) findViewById(R.id.dayBtn);
        monthBtn = (TextView) findViewById(R.id.monthBtn);
        addDataBtn = (TextView) findViewById(R.id.addDataBtn);

        weekBtn.setOnClickListener(this);
        monthBtn.setOnClickListener(this);
        addDataBtn.setOnClickListener(this);

        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);
        mChart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // Daniel (2016-11-29 15:46:23): X 방향으로는 차트 scale 불가능 하도록 처리
        mChart.setScaleXEnabled(true);
        // Daniel (2016-11-26 21:32:11): Y 방향으로는 차트 scale 불가능 하도록 처리
        mChart.setScaleYEnabled(true);
        // double tap 시 그래프 zoom 불가능 처리
        mChart.setDoubleTapToZoomEnabled(false);

        // Daniel (2016-11-26 21:25:32): 차트 위에 shadow 그리기 여부 (비추천 성능 50% 감소)
        mChart.setDrawBarShadow(false);

        // Daniel (2016-11-26 21:19:20): 차트 위에 value 그리기 여부
        mChart.setDrawValueAboveBar(true);

        // Daniel (2016-11-26 21:20:19): 차트 외부 border 그리기 여부
        mChart.setDrawBorders(false);

        // Daniel (2016-11-26 21:36:24): 보여줄 최대 차트 value 값 정하기
        // 만약 차트 value 가 지정한 값 이상일 경우, value 를 보여주지 않음
//        mChart.setMaxVisibleValueCount();

        mChart.setDrawGridBackground(false);

        // Daniel (2016-11-27 11:00:06): 해당 차트 종류에 따른 표시를 해주지 말 것.
        mChart.getLegend().setEnabled(false);

        // Daniel (2016-11-26 21:48:18): X-right 부분은 사용 x
        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setEnabled(true);

        // Daniel (2016-11-29 18:40:43): 차트 top 부분 round 하게 처리, radius 는 10.0f
        mChart.setDrawBarTopRoundEnable(true, 10f);

        // Grid background 설정 및 색상 선택
        mChart.setDrawGridBackground(true);
        mChart.setGridBackgroundColor(getResources().getColor(android.R.color.white));

        // Drag 시 highlight 불가능 하도록 처리
        mChart.setHighlightPerDragEnabled(false);

        // Highlight 처리시 group enable 처리!
        mChart.setHighlightXValueGroupEnable(true);
        // Highlight 된 부분만 draw 하도록 처리!
        mChart.setHighlightOnlyDrawValueEnable(true);
        // Highlight 시 해당 x group 필드 색칠하도록 설정
        mChart.setDrawXGroupBackgroundWhenHighlightedEnable(true);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart

//        final Random r = new Random();
//        int count = r.nextInt(5) + 1;
        int count = 7;

        setXAxisField(count, mCurrentGraphType);
        setYAxisField(mCurrentGraphType);
        updateDayData(count);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                for (IBarDataSet set : mChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                mChart.invalidate();
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
                mChart.notifyDataSetChanged();
                break;
            }
            case R.id.actionToggleBarBorders: {
                for (IBarDataSet set : mChart.getData().getDataSets())
                    ((BarDataSet) set).setBarBorderWidth(set.getBarBorderWidth() == 1.f ? 0.f : 1.f);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (mChart.getData() != null) {
                    mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
                    mChart.invalidate();
                }
                break;
            }
            case R.id.actionSave: {
                // mChart.saveToGallery("title"+System.currentTimeMillis());
                mChart.saveToPath("title" + System.currentTimeMillis(), "");
                break;
            }
            case R.id.animateX: {
                mChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(3000);
                break;
            }
            case R.id.animateXY: {
                mChart.animateXY(3000, 3000);
                break;
            }
        }
        return true;
    }

    /**
     * X 축 필드
     * @param labelCount
     * @param graphType
     */
    private void setXAxisField(int labelCount, GraphType graphType) {

        // Daniel (2016-11-26 21:39:26): 아래 해당 날짜 또는 월을 보여줘야 함
        IAxisValueFormatter xAxisFormatter = null;

        if (graphType == null || graphType == GraphType.Days) {
            xAxisFormatter = new DayAxisValueFormatter2(mChart);
        } else {
            xAxisFormatter = new MonthAxisValueFormatter(mChart);
        }
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTypeface(mTfLight);
        xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(labelCount);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setCenterAxisLabels(true);

        xAxis.setDrawGridLines(false);  // x축 grid 선 긋기
//        xAxis.setGridColor(getResources().getColor(android.R.color.transparent));   // grid 선 x축 색깔
//        xAxis.setGridLineWidth(3f);
        xAxis.setAxisLineColor(getResources().getColor(R.color.color_f1f1f1));   // Axis 선 x축 색깔
        xAxis.setAxisLineWidthInPixel(2f);

        xAxis.setTextSize(10f);
        xAxis.setTextColor(getResources().getColor(R.color.color_a2a2a2));
    }

    /**
     * Y 축 필드
     * @param graphType
     */
    private void setYAxisField(GraphType graphType) {
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(Typeface.DEFAULT);
//        leftAxis.setValueFormatter(new LargeValueFormatter());
        // Daniel (2016-11-29 17:22:41): 위 chart bar 위에 값을 표시하기 위한 최소 공간이 존재해야 한다.
        leftAxis.setSpaceTop(11f);
        // Daniel (2016-11-27 12:34:30): 최소 0.1부터 시작
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setGranularity(1.0f);

        if (graphType == null || graphType == GraphType.Days)
            leftAxis.setLabelCount(3);
        else
            leftAxis.setLabelCount(3);

        leftAxis.setDrawGridLines(true);    // y축 grid 선 긋기
        leftAxis.setGridColor(getResources().getColor(R.color.color_f1f1f1));  // grid 선 y축 색깔
        leftAxis.setGridLineWidthInPixel(2f);
        leftAxis.setDrawAxisLine(false);    // y축 axis 선 긋기
//        leftAxis.setAxisLineColor(getResources().getColor(android.R.color.transparent));    // Axis 선 y축 색깔
//        leftAxis.setAxisLineWidth(3f);

        leftAxis.setTextSize(8f);
        leftAxis.setTextColor(getResources().getColor(R.color.color_a2a2a2));
    }

    /**
     * 일 데이터 설정
     * @param maxGroupCount
     */
    private void updateDayData(int maxGroupCount) {

        float groupSpace = 0.28f;
        float barSpace = 0.06f; // x2 DataSet
        float barWidth = 0.3f; // x2 DataSet
        // (0.3 + 0.06) * 2 + 0.08 = 1.00 -> interval per "group"

        // column 은 최대 7개 (변동 가능)
        int groupCount = maxGroupCount < 1 ? 7 : maxGroupCount;

        // highlight 때 사용하기 위해서는 반드시 설정을 해주세요!
        mChart.setXGroupFieldCountWhenHighlighted(groupCount);

//        int startDay = (int) (System.currentTimeMillis() / (1000 * 60 * 60 * 24));
        int startDay = (int) (DateUtil.getMillisFromDate(2015, 2, 27) / (1000 * 60 * 60 * 24));
        int endDay = startDay + groupCount;

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<BarEntry> yVals2 = new ArrayList<>();

        Random r = new Random();
        int randomInt = 0;

        for (int i = startDay; i < endDay; i++) {
            randomInt = r.nextInt(5);
            yVals1.add(new BarEntry(i, randomInt * 3));
            randomInt = r.nextInt(10);
            yVals2.add(new BarEntry(i, randomInt * 3));
        }

        BarDataSet set1, set2;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {

            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) mChart.getData().getDataSetByIndex(1);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();

        } else {
            // create 2 DataSets
            set1 = new BarDataSet(yVals1, null);
            set1.setColor(getResources().getColor(R.color.color_19a6f3));
            set1.setValueTextSize(8f);        // bar 에 표시되는 value size
            // Daniel (2016-11-29 17:30:19): bar 에 표시되는 value 형식
            set1.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.valueOf((int) value);
                }
            });
            // Daniel (2016-11-29 17:30:36): bar 에 표시되는 value color
            set1.setValueTextColor(getResources().getColor(R.color.color_19a6f3));
            // value type face 처리
            set1.setValueTypeface(Typeface.DEFAULT_BOLD);
            // Daniel (2016-11-29 18:25:09): bar 에 표시되는 highlight color
            set1.setHighLightColor(getResources().getColor(R.color.color_19a6f3));
            set1.setHighLightAlpha(255);  // bar 에 표시되는 highlight alpha

            set2 = new BarDataSet(yVals2, null);
            set2.setColor(getResources().getColor(R.color.color_00dba2));
            set2.setValueTextSize(8f);        // bar 에 표시되는 value size
            // Daniel (2016-11-29 17:30:19): bar 에 표시되는 value 형식
            set2.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.valueOf((int) value);
                }
            });
            // Daniel (2016-11-29 17:30:36): bar 에 표시되는 value color
            set2.setValueTextColor(getResources().getColor(R.color.color_00dba2));
            // value type face 처리
            set2.setValueTypeface(Typeface.DEFAULT_BOLD);

            // Daniel (2016-11-29 18:25:09): bar 에 표시되는 highlight color
            set2.setHighLightColor(getResources().getColor(R.color.color_00dba2));
            set2.setHighLightAlpha(255);  // bar 에 표시되는 highlight alpha

            BarData data = new BarData(set1, set2);
            // Daniel (2016-11-30 18:40:33): 참고로 공통으로 데이터를 처리할 수도 있다.
//            data.setValueFormatter(new LargeValueFormatter());
//            data.setValueTypeface(mTfLight);

            mChart.setData(data);
        }

        // specify the width each bar should have
        mChart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        mChart.getXAxis().setAxisMinimum(startDay);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        mChart.getXAxis().setAxisMaximum(startDay + mChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        mChart.groupBars(startDay, groupSpace, barSpace);

        // HIGHLIGHT 초기화 부분 처리
        // Highlight 위에 그려지는 value 초기화
        mChart.setHighlightOnlyDrawValueIndex(-1, -1);
        // 차트 touch 시 마지막 Highlight 부분 초기화
        mChart.getOnTouchListener().setLastHighlighted(null);
        mChart.getOnTouchListener().setLastHighlightArray(null);
        // Daniel (2016-12-11 23:19:48): Highlight 초기화
        mChart.highlightValues(null);

        mChart.invalidate();
        // Daniel (2016-11-26 21:21:43): XY축 방향으로 차트 애니메이션 그리기 설정
        mChart.animateXY(500, 500);
    }

    /**
     * 월 데이터 설정
     * @param maxGroupCount
     */
    private void updateMonthData(int maxGroupCount) {

        float groupSpace = 0.28f;
        float barSpace = 0.06f; // x2 DataSet
        float barWidth = 0.3f; // x2 DataSet
        // (0.3 + 0.06) * 2 + 0.08 = 1.00 -> interval per "group"

        // column 은 최대 7개 (변동 가능)
        int groupCount = maxGroupCount < 1 ? 7 : maxGroupCount;

        // highlight 때 사용하기 위해서는 반드시 설정을 해주세요!
        mChart.setXGroupFieldCountWhenHighlighted(groupCount);

        int startMonth = DateUtil.getCurrentMonthOfYear() - groupCount + 1 + 12 * groupCount ;
        int endDay = startMonth + groupCount;

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<BarEntry> yVals2 = new ArrayList<>();

        Random r = new Random();
        int randomInt = 0;

        for (int i = startMonth; i < endDay; i++) {
            randomInt = r.nextInt(100);
            yVals1.add(new BarEntry(i, randomInt * 3));
            randomInt = r.nextInt(50);
            yVals2.add(new BarEntry(i, randomInt * 3));
        }

        BarDataSet set1, set2;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {

            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) mChart.getData().getDataSetByIndex(1);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();

        } else {
            // create 2 DataSets
            set1 = new BarDataSet(yVals1, null);
            set1.setColor(getResources().getColor(R.color.color_19a6f3));
            set1.setValueTextSize(8f);        // bar 에 표시되는 value size
            // Daniel (2016-11-29 17:30:19): bar 에 표시되는 value 형식
            set1.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.valueOf((int) value);
                }
            });
            // Daniel (2016-11-29 17:30:36): bar 에 표시되는 value color
            set1.setValueTextColor(getResources().getColor(R.color.color_19a6f3));
            // value type face 처리
            set1.setValueTypeface(Typeface.DEFAULT_BOLD);
            // Daniel (2016-11-29 18:25:09): bar 에 표시되는 highlight color
            set1.setHighLightColor(getResources().getColor(android.R.color.holo_red_light));
            set1.setHighLightAlpha(0);  // bar 에 표시되는 highlight alpha

            set2 = new BarDataSet(yVals2, null);
            set2.setColor(getResources().getColor(R.color.color_00dba2));
            set2.setValueTextSize(8f);        // bar 에 표시되는 value size
            // Daniel (2016-11-29 17:30:19): bar 에 표시되는 value 형식
            set2.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.valueOf((int) value);
                }
            });
            // Daniel (2016-11-29 17:30:36): bar 에 표시되는 value color
            set2.setValueTextColor(getResources().getColor(R.color.color_00dba2));
            // value type face 처리
            set2.setValueTypeface(Typeface.DEFAULT_BOLD);

            // Daniel (2016-11-29 18:25:09): bar 에 표시되는 highlight color
            set2.setHighLightColor(getResources().getColor(android.R.color.holo_red_light));
            set2.setHighLightAlpha(0);  // bar 에 표시되는 highlight alpha

            BarData data = new BarData(set1, set2);
            // Daniel (2016-11-30 18:40:33): 참고로 공통으로 데이터를 처리할 수도 있다.
//            data.setValueFormatter(new LargeValueFormatter());
//            data.setValueTypeface(mTfLight);

            mChart.setData(data);
        }

        // specify the width each bar should have
        mChart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        mChart.getXAxis().setAxisMinimum(startMonth);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        mChart.getXAxis().setAxisMaximum(startMonth + mChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        mChart.groupBars(startMonth, groupSpace, barSpace);

        // HIGHLIGHT 초기화 부분 처리
        // Highlight 위에 그려지는 value 초기화
        mChart.setHighlightOnlyDrawValueIndex(-1, -1);
        // 차트 touch 시 마지막 Highlight 부분 초기화
        mChart.getOnTouchListener().setLastHighlighted(null);
        mChart.getOnTouchListener().setLastHighlightArray(null);
        // Daniel (2016-12-11 23:19:48): Highlight 초기화
        mChart.highlightValues(null);

        mChart.invalidate();
        // Daniel (2016-11-26 21:21:43): Y 축 방향으로 차트 애니메이션 그리기 설정
        mChart.animateXY(500, 500);
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        DANIEL.log().d("Selected index : " + h.getDataSetIndex());
        DANIEL.log().d("Selected X값 : " + (int) e.getX());
        DANIEL.log().d("Selected Y값 : " + (int) e.getY());
    }

    @Override
    public void onNothingSelected() {
        DANIEL.log().d("Nothing selected.");
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        switch (v.getId()) {
            case R.id.dayBtn: {
                mCurrentGraphType = GraphType.Days;
                final Random r = new Random();
                int count = r.nextInt(5) + 1;
//                int count = 7;

                setXAxisField(count, mCurrentGraphType);
                setYAxisField(mCurrentGraphType);
                updateDayData(count);
                break;
            }
            case R.id.monthBtn: {
                mCurrentGraphType = GraphType.Months;
                final Random r = new Random();
                int count = r.nextInt(5) + 1;
//                int count = 7;

                setXAxisField(count, mCurrentGraphType);
                setYAxisField(mCurrentGraphType);
                updateMonthData(count);
                break;
            }
        }
    }
}