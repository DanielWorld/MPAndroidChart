package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.BarHighlighter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;

/**
 * Chart that draws bars.
 *
 * @author Philipp Jahoda
 */
public class BarChart extends BarLineChartBase<BarData> implements BarDataProvider {

    /**
     * flag that indicates whether the highlight should be full-bar oriented, or single-value?
     */
    protected boolean mHighlightFullBarEnabled = false;

    /**
     * if set to true, all values are drawn above their bars, instead of below their top
     */
    private boolean mDrawValueAboveBar = true;

    /**
     * if set to true, a grey area is drawn behind each bar that indicates the maximum value
     */
    private boolean mDrawBarShadow = false;

    /**
     * 만약 true 로 설정될 경우, chart bar 의 top 부분이 지정된 radius 만큼 그린다.
     * <p>{@link android.os.Build.VERSION_CODES#LOLLIPOP} 이상만 적용 가능</p>
     */
    private boolean mDrawBarTopRoundEnable = false;

    /**
     * {@link BarChart#isDrawBarTopRoundEnabled()} true 일 경우 chart bar 의 top 부분을 해당 radius 만큼
     * 둥글게 처리한다.
     */
    private float mTopRoundRadius;

    /**
     * {@link BarChart#isHighlightOnlyDrawValueEnabled()} true 일 경우, 유저가 highlight 시도시
     * 해당 highlight 된 chart 만 value 를 그려낸다.
     */
    private boolean mHighlightOnlyDrawValueEnable = false;

    /**
     * {@link BarChart#isDrawXGroupBackgroundWhenHighlighted()} true 일 경우, 유저가 highlight 시도시
     * 해당 x group field background 를 색칠한다.
     */
    private boolean mDrawXGroupBackgroundWhenHighlighted = false;

    /**
     * X field 의 그룹 갯수
     */
    private int mXGroupFieldCountWhenHighlighted = 0;

    private boolean mFitBars = false;

    public BarChart(Context context) {
        super(context);
    }

    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new BarChartRenderer(this, mAnimator, mViewPortHandler);

        setHighlighter(new BarHighlighter(this));

        getXAxis().setSpaceMin(0.5f);
        getXAxis().setSpaceMax(0.5f);
    }

    @Override
    protected void calcMinMax() {

        if (mFitBars) {
            mXAxis.calculate(mData.getXMin() - mData.getBarWidth() / 2f, mData.getXMax() + mData.getBarWidth() / 2f);
        } else {
            mXAxis.calculate(mData.getXMin(), mData.getXMax());
        }

        // calculate axis range (min / max) according to provided data
        mAxisLeft.calculate(mData.getYMin(YAxis.AxisDependency.LEFT), mData.getYMax(YAxis.AxisDependency.LEFT));
        mAxisRight.calculate(mData.getYMin(YAxis.AxisDependency.RIGHT), mData.getYMax(YAxis.AxisDependency
                .RIGHT));
    }

    /**
     * Returns the Highlight object (contains x-index and DataSet index) of the selected value at the given touch
     * point
     * inside the BarChart.
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public Highlight getHighlightByTouchPoint(float x, float y) {

        if (mData == null) {
            Log.e(LOG_TAG, "Can't select by touch. No data set.");
            return null;
        } else {
            Highlight h = getHighlighter().getHighlight(x, y);
            if (h == null || !isHighlightFullBarEnabled()) return h;

            // For isHighlightFullBarEnabled, remove stackIndex
            return new Highlight(h.getX(), h.getY(),
                    h.getXPx(), h.getYPx(),
                    h.getDataSetIndex(), -1, h.getAxis());
        }
    }

    /**
     * Returns the bounding box of the specified Entry in the specified DataSet. Returns null if the Entry could not be
     * found in the charts data.  Performance-intensive code should use void getBarBounds(BarEntry, RectF) instead.
     *
     * @param e
     * @return
     */
    public RectF getBarBounds(BarEntry e) {

        RectF bounds = new RectF();
        getBarBounds(e, bounds);

        return bounds;
    }

    /**
     * The passed outputRect will be assigned the values of the bounding box of the specified Entry in the specified DataSet.
     * The rect will be assigned Float.MIN_VALUE in all locations if the Entry could not be found in the charts data.
     *
     * @param e
     * @return
     */
    public void getBarBounds(BarEntry e, RectF outputRect) {

        RectF bounds = outputRect;

        IBarDataSet set = mData.getDataSetForEntry(e);

        if (set == null) {
            bounds.set(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
            return;
        }

        float y = e.getY();
        float x = e.getX();

        float barWidth = mData.getBarWidth();

        float left = x - barWidth / 2f;
        float right = x + barWidth / 2f;
        float top = y >= 0 ? y : 0;
        float bottom = y <= 0 ? y : 0;

        bounds.set(left, top, right, bottom);

        getTransformer(set.getAxisDependency()).rectValueToPixel(outputRect);
    }

    /**
     * If set to true, all values are drawn above their bars, instead of below their top.
     *
     * @param enabled
     */
    public void setDrawValueAboveBar(boolean enabled) {
        mDrawValueAboveBar = enabled;
    }

    /**
     * returns true if drawing values above bars is enabled, false if not
     *
     * @return
     */
    public boolean isDrawValueAboveBarEnabled() {
        return mDrawValueAboveBar;
    }

    /**
     * If set to true, a grey area is drawn behind each bar that indicates the maximum value. Enabling his will reduce
     * performance by about 50%.
     *
     * @param enabled
     */
    public void setDrawBarShadow(boolean enabled) {
        mDrawBarShadow = enabled;
    }

    /**
     * returns true if drawing shadows (maxvalue) for each bar is enabled, false if not
     *
     * @return
     */
    public boolean isDrawBarShadowEnabled() {
        return mDrawBarShadow;
    }

    /**
     * Set this to true to make the highlight operation full-bar oriented, false to make it highlight single values (relevant
     * only for stacked). If enabled, highlighting operations will highlight the whole bar, even if only a single stack entry
     * was tapped.
     * Default: false
     *
     * @param enabled
     */
    public void setHighlightFullBarEnabled(boolean enabled) {
        mHighlightFullBarEnabled = enabled;
    }

    /**
     * @return true the highlight operation is be full-bar oriented, false if single-value
     */
    @Override
    public boolean isHighlightFullBarEnabled() {
        return mHighlightFullBarEnabled;
    }

    /**
     * chart bar 의 top 부분을 round 처리를 할 것인지 여부 처리 <br>
     *     {@link android.os.Build.VERSION_CODES#LOLLIPOP} 이상 버전에서만 적용 가능
     * @param enabled
     * @param radius 반드시 0 보다 큰 유효값이 와야함. 안그러면 {@link IllegalArgumentException} 에러 발생
     *               또한, 비상적으로 큰 값이 올 경우 적용이 안될 수 있음.
     */
    public void setDrawBarTopRoundEnable(boolean enabled, float radius) {
        if (radius <= 0) throw new IllegalArgumentException("top round radius should be larger than 0");

        mDrawBarTopRoundEnable = enabled;
        mTopRoundRadius = radius;
    }

    /**
     * @return true 일 경우, chart bar 의 top 부분은 round 처리가 된다. 롤리팝 미만 기기는 적용 안됨.
     */
    @Override
    public boolean isDrawBarTopRoundEnabled() {
        return mDrawBarTopRoundEnable;
    }

    /**
     * char bar top 부분 radius
     * <p>{@link BarChart#isDrawBarTopRoundEnabled()} true 의 경우, 해당 radius 값 가져오기</p>
     * @return
     */
    @Override
    public float getDrawBarTopRoundRadius() {
        return mTopRoundRadius;
    }

    /**
     * 유저가 highlight 시도할 때, 해당 highlight 된 부분만 value 를 draw 할 것인지 여부
     * @param enabled
     */
    public void setHighlightOnlyDrawValueEnable(boolean enabled) {
        mHighlightOnlyDrawValueEnable = enabled;
    }

    /**
     * 유저가 highlight 시도할 때, 해당 highlight 된 chart 만 value 를 그려내는지 여부.
     * <p>highlight 된 chart 만 value 를 그려낼 경우, highlight 안된 chart 들은 자동으로 value 를 숨긴다.</p>
     * @return
     */
    @Override
    public boolean isHighlightOnlyDrawValueEnabled() {
        return mHighlightOnlyDrawValueEnable;
    }

    @Override
    public int getHighlightOnlyDrawValueFirstIndex() {
        return Chart.highlightOnlyDrawValueFirstIndex;
    }

    @Override
    public int getHighlightOnlyDrawValueLastIndex() {
        return Chart.highlightOnlyDrawValueLastIndex;
    }

    @Override
    public boolean isDrawXGroupBackgroundWhenHighlighted() {
        return isHighlightXValueGroupEnabled() && mDrawXGroupBackgroundWhenHighlighted;
    }

    @Override
    public int getXGroupFieldCountWhenHighlighted() {
        return mXGroupFieldCountWhenHighlighted;
    }

    /**
     * X 필드의 그룹 갯수 설정
     * @param groupCount
     */
    public void setXGroupFieldCountWhenHighlighted(int groupCount) {
        if (groupCount > 0) mXGroupFieldCountWhenHighlighted = groupCount;
    }

    /**
     * {@link Chart#isHighlightXValueGroupEnabled()} 가 true 일 경우 동작하며, 유저가 highlight 처리시
     * 해당 x group field 배경 처리 여부 설정
     * @param enabled
     * @return
     */
    public void setDrawXGroupBackgroundWhenHighlightedEnable(boolean enabled) {
        mDrawXGroupBackgroundWhenHighlighted = enabled;
    }

    /**
     * Highlights the value at the given x-value in the given DataSet. Provide
     * -1 as the dataSetIndex to undo all highlighting.
     *
     * @param x
     * @param dataSetIndex
     * @param stackIndex   the index inside the stack - only relevant for stacked entries
     */
    public void highlightValue(float x, int dataSetIndex, int stackIndex) {
        highlightValue(new Highlight(x, dataSetIndex, stackIndex), false);
    }

    @Override
    public BarData getBarData() {
        return mData;
    }

    /**
     * Adds half of the bar width to each side of the x-axis range in order to allow the bars of the barchart to be
     * fully displayed.
     * Default: false
     *
     * @param enabled
     */
    public void setFitBars(boolean enabled) {
        mFitBars = enabled;
    }

    /**
     * Groups all BarDataSet objects this data object holds together by modifying the x-value of their entries.
     * Previously set x-values of entries will be overwritten. Leaves space between bars and groups as specified
     * by the parameters.
     * Calls notifyDataSetChanged() afterwards.
     *
     * @param fromX      the starting point on the x-axis where the grouping should begin
     * @param groupSpace the space between groups of bars in values (not pixels) e.g. 0.8f for bar width 1f
     * @param barSpace   the space between individual bars in values (not pixels) e.g. 0.1f for bar width 1f
     */
    public void groupBars(float fromX, float groupSpace, float barSpace) {

        if (getBarData() == null) {
            throw new RuntimeException("You need to set data for the chart before grouping bars.");
        } else {
            getBarData().groupBars(fromX, groupSpace, barSpace);
            notifyDataSetChanged();
        }
    }
}
