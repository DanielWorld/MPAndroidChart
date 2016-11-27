package com.xxmassdeveloper.mpchartexample;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.xxmassdeveloper.mpchartexample.custom.DayAxisValueFormatter2;
import com.xxmassdeveloper.mpchartexample.custom.MonthAxisValueFormatter;
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;
import java.util.Random;

/**
 * Copyright (c) 2014-2016 op7773hons@gmail.com
 * Created by Daniel Park on 2016-11-27.
 */

public class BarChartActivityMultiDataset2 extends DemoBase implements
        OnChartValueSelectedListener, View.OnClickListener {

    private final String TAG = "OKAY";

    enum GraphType {
        Days, Months
    }
    GraphType mCurrentGraphType = GraphType.Days;

    private BarChart mChart;
    private TextView weekBtn, monthBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart2);

        weekBtn = (TextView) findViewById(R.id.dayBtn);
        monthBtn = (TextView) findViewById(R.id.monthBtn);

        weekBtn.setOnClickListener(this);
        monthBtn.setOnClickListener(this);

        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);
        mChart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // Daniel (2016-11-26 21:32:11): Y 방향으로는 차트 scale 불가능 하도록 처리
        mChart.setScaleYEnabled(false);

        // Daniel (2016-11-26 21:25:32): 차트 위에 shadow 그리기 여부 (비추천 성능 50% 감소)
        mChart.setDrawBarShadow(false);

        // Daniel (2016-11-26 21:19:20): 차트 위에 value 그리기 여부
        mChart.setDrawValueAboveBar(true);

        // Daniel (2016-11-26 21:20:19): 차트 외부 border 그리기 여부
        mChart.setDrawBorders(false);

        // Daniel (2016-11-26 21:36:24): 보여줄 최대 차트 value 값 정하기
        // 만약 차트 value 가 지정한 값 이상일 경우, value 를 보여주지 않음
        mChart.setMaxVisibleValueCount(-1);

        mChart.setDrawGridBackground(false);

        // Daniel (2016-11-27 11:00:06): 해당 차트 종류에 따른 표시를 해주지 말 것.
        mChart.getLegend().setEnabled(false);

        // Daniel (2016-11-26 21:48:18): X-right 부분은 사용 x
        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setEnabled(true);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart



        final Random r = new Random();

        final Handler h = new Handler();

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = r.nextInt(5) + 1;

                setXAxisField(count, mCurrentGraphType);
                setYAxisField(mCurrentGraphType);
                updateDayData(count);
                h.postDelayed(this, 5000);
            }
        }, 1);
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
        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(labelCount);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setCenterAxisLabels(true);
    }

    /**
     * Y 축 필드
     * @param graphType
     */
    private void setYAxisField(GraphType graphType) {
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
//        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setSpaceTop(0f);
        // Daniel (2016-11-27 12:34:30): 최소 0.1부터 시작
        leftAxis.setAxisMinimum(0.1f); // this replaces setStartAtZero(true)

        if (graphType == null || graphType == GraphType.Days)
            leftAxis.setLabelCount(3);
        else
            leftAxis.setLabelCount(3);

        leftAxis.setDrawGridLines(true);    // grid 선 긋기
        leftAxis.setGridColor(getResources().getColor(android.R.color.holo_red_dark));  // grid 선 색깔
        leftAxis.setGridLineWidth(1.0f);     // grid 선 굵기 (dp)

        leftAxis.setAxisLineColor(getResources().getColor(android.R.color.holo_orange_dark));    // Axis 선 색깔
    }

    /**
     * 데이터 설정
     * @param maxGroupCount
     */
    private void updateDayData(int maxGroupCount) {

        float groupSpace = 0.28f;
        float barSpace = 0.06f; // x2 DataSet
        float barWidth = 0.3f; // x2 DataSet
        // (0.3 + 0.06) * 2 + 0.08 = 1.00 -> interval per "group"

        // column 은 최대 7개 (변동 가능)
        int groupCount = maxGroupCount < 1 ? 7 : maxGroupCount;
        int startDay = 311;
        int endDay = startDay + groupCount;

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<BarEntry> yVals2 = new ArrayList<>();

        Random r = new Random();
        int randomInt = 0;

        for (int i = startDay; i < endDay; i++) {
            randomInt = r.nextInt(4);
            yVals1.add(new BarEntry(i, randomInt * 7));
            randomInt = r.nextInt(4);
            yVals2.add(new BarEntry(i, randomInt * 5));
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
            set1.setColor(getResources().getColor(android.R.color.background_dark));
            set2 = new BarDataSet(yVals2, null);
            set2.setColor(Color.rgb(164, 228, 251));

            BarData data = new BarData(set1, set2);
//            data.setValueFormatter(new LargeValueFormatter());
            data.setValueTypeface(mTfLight);

            mChart.setData(data);
        }

        // specify the width each bar should have
        mChart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        mChart.getXAxis().setAxisMinimum(startDay);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        mChart.getXAxis().setAxisMaximum(startDay + mChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        mChart.groupBars(startDay, groupSpace, barSpace);

        mChart.invalidate();
        // Daniel (2016-11-26 21:21:43): Y 축 방향으로 차트 애니메이션 그리기 설정
        mChart.animateY(1500);
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.d(TAG, "Selected: " + e.toString() + ", dataSet: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.d(TAG, "Nothing selected.");
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        switch (v.getId()) {
            case R.id.dayBtn:
                mCurrentGraphType = GraphType.Days;
                break;
            case R.id.monthBtn:
                mCurrentGraphType = GraphType.Months;
                break;
        }
    }
}