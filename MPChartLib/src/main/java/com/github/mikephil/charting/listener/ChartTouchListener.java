package com.github.mikephil.charting.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.DANIEL;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

import java.util.Arrays;
import java.util.List;

/**
 * Created by philipp on 12/06/15.
 */
public abstract class ChartTouchListener<T extends Chart<?>> extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

    public enum ChartGesture {
        NONE, DRAG, X_ZOOM, Y_ZOOM, PINCH_ZOOM, ROTATE, SINGLE_TAP, DOUBLE_TAP, LONG_PRESS, FLING
    }

    /**
     * the last touch gesture that has been performed
     **/
    protected ChartGesture mLastGesture = ChartGesture.NONE;

    // states
    protected static final int NONE = 0;
    protected static final int DRAG = 1;
    protected static final int X_ZOOM = 2;
    protected static final int Y_ZOOM = 3;
    protected static final int PINCH_ZOOM = 4;
    protected static final int POST_ZOOM = 5;
    protected static final int ROTATE = 6;

    /**
     * integer field that holds the current touch-state
     */
    protected int mTouchMode = NONE;

    /**
     * the last highlighted object (via touch)
     */
    protected Highlight mLastHighlighted;

    /**
     * the gesturedetector used for detecting taps and longpresses, ...
     */
    protected GestureDetector mGestureDetector;

    /**
     * the chart the listener represents
     */
    protected T mChart;

    public ChartTouchListener(T chart) {
        this.mChart = chart;

        mGestureDetector = new GestureDetector(chart.getContext(), this);
    }

    /**
     * Calls the OnChartGestureListener to do the start callback
     *
     * @param me
     */
    public void startAction(MotionEvent me) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null)
            l.onChartGestureStart(me, mLastGesture);
    }

    /**
     * Calls the OnChartGestureListener to do the end callback
     *
     * @param me
     */
    public void endAction(MotionEvent me) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null)
            l.onChartGestureEnd(me, mLastGesture);
    }

    /**
     * Sets the last value that was highlighted via touch.
     *
     * @param high
     */
    public void setLastHighlighted(Highlight high) {
        mLastHighlighted = high;
    }

    /**
     * returns the touch mode the listener is currently in
     *
     * @return
     */
    public int getTouchMode() {
        return mTouchMode;
    }

    /**
     * Returns the last gesture that has been performed on the chart.
     *
     * @return
     */
    public ChartGesture getLastGesture() {
        return mLastGesture;
    }


    /**
     * Perform a highlight operation.
     *
     * @param e
     */
    protected void performHighlight(Highlight h, MotionEvent e) {

        if (h == null || h.equalTo(mLastHighlighted)) {
            mChart.highlightValue(null, true);
            mLastHighlighted = null;
        } else {
            mChart.highlightValue(h, true);
            mLastHighlighted = h;
        }
    }

    float[] mLastHighlightArray;

    protected void performHighlightList(List<Highlight> highlightList, MotionEvent e) {
        if (highlightList == null) {
            mChart.highlightValues(null);
            mLastHighlightArray = null;

            // Highlight only draw value index initialize 처리
            mChart.setHighlightOnlyDrawValueIndex(-1, -1);
        }
        else if (mLastHighlightArray != null && mLastHighlightArray.length == highlightList.size()) {
            for (float lastHighX : mLastHighlightArray) {
                for (Highlight currentH : highlightList) {
                    if (currentH.getX() == lastHighX) {
                        DANIEL.log().e("contains!");
                        mChart.highlightValues(null);
                        mLastHighlightArray = null;

                        // Highlight only draw value index initialize 처리
                        mChart.setHighlightOnlyDrawValueIndex(-1, -1);
                        return;
                    }
                }
            }
            DANIEL.log().e("Last high x (0) : " + mLastHighlightArray[0]);
            DANIEL.log().e("Last high x (1) : " + mLastHighlightArray[1]);
            DANIEL.log().e("highlightList x (0) : " + highlightList.get(0).getX());
            DANIEL.log().e("highlightList x (1) : " + highlightList.get(1).getX());

            mChart.highlightValues(highlightList.toArray(new Highlight[highlightList.size()]));

            mLastHighlightArray = new float[highlightList.size()];
            for (int i = 0 ; i < highlightList.size(); i++) {
                mLastHighlightArray[i] = highlightList.get(i).getX();
            }

            calculateHighlightOnlyDrawValueIndex();
        }
        else {
            mChart.highlightValues(highlightList.toArray(new Highlight[highlightList.size()]));
            mLastHighlightArray = new float[highlightList.size()];
            for (int i = 0 ; i < highlightList.size(); i++) {
                mLastHighlightArray[i] = highlightList.get(i).getX();
            }

            calculateHighlightOnlyDrawValueIndex();
        }
    }

    /**
     * {@link Chart#setHighlightOnlyDrawValueIndex(int, int)} 에서 index 를 계산하기 위한 method
     * <p>{@link BarDataProvider#isHighlightOnlyDrawValueEnabled()} 가 true 일 경우에만 유효한 기능</p>
     */
    private void calculateHighlightOnlyDrawValueIndex() {
        int firstIndex = 0, lastIndex = 0;

        for (IDataSet barDataSet : mChart.getData().getDataSets()) {
            for (int i = 0; i < mLastHighlightArray.length; i++) {
                List<Entry> entries = barDataSet.getEntriesForXValue(mLastHighlightArray[i]);

                for (Entry entry : entries) {
                    DANIEL.log().d("highlighted entry getX() : " + entry.getX());
//                    if (firstIndex != 0)
//                        firstIndex = barDataSet.getEntryIndex(entry);
//                    else {
                        lastIndex = barDataSet.getEntryIndex(entry);
                        firstIndex = lastIndex;
//                    }
                }
            }
        }

        DANIEL.log().v("firstIndex : " + firstIndex + "\nlastIndex : " + lastIndex);

        mChart.setHighlightOnlyDrawValueIndex(firstIndex, lastIndex);
    }

    /**
     * returns the distance between two points
     *
     * @param eventX
     * @param startX
     * @param eventY
     * @param startY
     * @return
     */
    protected static float distance(float eventX, float startX, float eventY, float startY) {
        float dx = eventX - startX;
        float dy = eventY - startY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
