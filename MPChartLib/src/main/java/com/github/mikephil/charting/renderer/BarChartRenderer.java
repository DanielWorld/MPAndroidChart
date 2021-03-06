
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;

import com.github.mikephil.charting.DANIEL;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class BarChartRenderer extends BarLineScatterCandleBubbleRenderer {

    protected BarDataProvider mChart;

    /**
     * the rect object that is used for drawing the bars
     */
    protected RectF mBarRect = new RectF();

    protected BarBuffer[] mBarBuffers;

    protected Paint mShadowPaint;
    protected Paint mBarBorderPaint;

    public BarChartRenderer(BarDataProvider chart, ChartAnimator animator,
                            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(Color.rgb(0, 0, 0));
        // set alpha after color
        mHighlightPaint.setAlpha(120);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);

        mBarBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarBorderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void initBuffers() {

        BarData barData = mChart.getBarData();
        mBarBuffers = new BarBuffer[barData.getDataSetCount()];

        for (int i = 0; i < mBarBuffers.length; i++) {
            IBarDataSet set = barData.getDataSetByIndex(i);
            mBarBuffers[i] = new BarBuffer(set.getEntryCount() * 4 * (set.isStacked() ? set.getStackSize() : 1),
                    barData.getDataSetCount(), set.isStacked());
        }
    }

    @Override
    public void drawData(Canvas c) {

        BarData barData = mChart.getBarData();

        for (int i = 0; i < barData.getDataSetCount(); i++) {

            IBarDataSet set = barData.getDataSetByIndex(i);

            if (set.isVisible()) {
                drawDataSet(c, set, i);
            }
        }
    }

    private RectF mBarShadowRectBuffer = new RectF();

    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {
            mShadowPaint.setColor(dataSet.getBarShadowColor());

            BarData barData = mChart.getBarData();

            final float barWidth = barData.getBarWidth();
            final float barWidthHalf = barWidth / 2.0f;
            float x;

            for (int i = 0, count = Math.min((int)(Math.ceil((float)(dataSet.getEntryCount()) * phaseX)), dataSet.getEntryCount());
                i < count;
                i++) {

                BarEntry e = dataSet.getEntryForIndex(i);

                x = e.getX();

                mBarShadowRectBuffer.left = x - barWidthHalf;
                mBarShadowRectBuffer.right = x + barWidthHalf;

                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                    break;

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();

                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }
        }

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        final boolean isSingleColor = dataSet.getColors().size() == 1;

        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.getColor());
        }

        for (int j = 0; j < buffer.size(); j += 4) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                continue;

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break;

            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4));
            }

            if (mChart.isDrawBarTopRoundEnabled() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && buffer.buffer[j + 1] + mChart.getDrawBarTopRoundRadius() < buffer.buffer[j + 3]) {

                final float drawBarTopRoundRadius = mChart.getDrawBarTopRoundRadius();

                c.drawRoundRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], drawBarTopRoundRadius, drawBarTopRoundRadius, mRenderPaint);
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1] + drawBarTopRoundRadius, buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint);
            }
            //======================================================================================================================================
            else if (buffer.buffer[j + 1] == buffer.buffer[j + 3]) {
                // Daniel (2016-12-01 14:13:33): 미안하지만 top 과 bottom 이 동일하다면 사실상 y값 0이나 다름이 없음. 2 ~ 3px 정도 그려줘야 함.. 그럴필요 없다면 이 부분 block 코멘트 처리
                c.drawRect(buffer.buffer[j], (buffer.buffer[j + 1] - 2), buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint);
            }
            //======================================================================================================================================
            else {
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint);
            }

            if (drawBorder) {
                if (mChart.isDrawBarTopRoundEnabled() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                        && buffer.buffer[j + 1] + mChart.getDrawBarTopRoundRadius() < buffer.buffer[j + 3]) {

                    final float drawBarTopRoundRadius = mChart.getDrawBarTopRoundRadius();

                    c.drawRoundRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], drawBarTopRoundRadius, drawBarTopRoundRadius, mBarBorderPaint);
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1] + drawBarTopRoundRadius, buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mBarBorderPaint);
                }
                else {
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mBarBorderPaint);
                }

            }
        }
    }

    protected void prepareBarHighlight(float x, float y1, float y2, float barWidthHalf, Transformer trans) {

        float left = x - barWidthHalf;
        float right = x + barWidthHalf;
        float top = y1;
        float bottom = y2;

        mBarRect.set(left, top, right, bottom);

        trans.rectToPixelPhase(mBarRect, mAnimator.getPhaseY());
    }

    @Override
    public void drawValues(Canvas c) {

        // if values are drawn
        if (isDrawingValuesAllowed(mChart)) {

            List<IBarDataSet> dataSets = mChart.getBarData().getDataSets();

            final float valueOffsetPlus = Utils.convertDpToPixel(4.5f);
            float posOffset = 0f;
            float negOffset = 0f;
            boolean drawValueAboveBar = mChart.isDrawValueAboveBarEnabled();

            // Daniel (2016-11-30 15:14:49): BarData set 에 따라서 value 를 그려내야 함.
            for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {

                IBarDataSet dataSet = dataSets.get(i);

                if (!shouldDrawValues(dataSet))
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                boolean isInverted = mChart.isInverted(dataSet.getAxisDependency());

                // calculate the correct offset depending on the draw position of
                // the value
                float valueTextHeight = Utils.calcTextHeight(mValuePaint, "8");
                posOffset = (drawValueAboveBar ? -valueOffsetPlus : valueTextHeight + valueOffsetPlus);
                negOffset = (drawValueAboveBar ? valueTextHeight + valueOffsetPlus : -valueOffsetPlus);

                if (isInverted) {
                    posOffset = -posOffset - valueTextHeight;
                    negOffset = -negOffset - valueTextHeight;
                }

                // get the buffer
                BarBuffer buffer = mBarBuffers[i];

                final float phaseY = mAnimator.getPhaseY();

                // if only single values are drawn (sum)
                if (!dataSet.isStacked()) {
                    for (int j = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += 4) {

                        // Daniel (2016-11-30 16:58:46): highlight 된 부분만 draw value 처리 여부 flag 체크
                        if (mChart.isHighlightOnlyDrawValueEnabled()) {
                            if (j < mChart.getHighlightOnlyDrawValueFirstIndex() * 4) continue;
                            if (j > mChart.getHighlightOnlyDrawValueLastIndex() * 4) continue;
                        }

                        float x = (buffer.buffer[j] + buffer.buffer[j + 2]) / 2f;

                        if (!mViewPortHandler.isInBoundsRight(x))
                            break;

                        if (!mViewPortHandler.isInBoundsY(buffer.buffer[j + 1])
                                || !mViewPortHandler.isInBoundsLeft(x))
                            continue;

                        BarEntry entry = dataSet.getEntryForIndex(j / 4);
                        float val = entry.getY();

                        drawValue(c, dataSet.getValueFormatter(), val, entry, i, x,
                                val >= 0 ? (buffer.buffer[j + 1] + posOffset) : (buffer.buffer[j + 3] + negOffset),
                                dataSet.getValueTextColor(j / 4));
                    }

                    // if we have stacks
                } else {
                    Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                    int bufferIndex = 0;
                    int index = 0;

                    while (index < dataSet.getEntryCount() * mAnimator.getPhaseX()) {

                        BarEntry entry = dataSet.getEntryForIndex(index);

                        float[] vals = entry.getYVals();
                        float x = (buffer.buffer[bufferIndex] + buffer.buffer[bufferIndex + 2]) / 2f;

                        int color = dataSet.getValueTextColor(index);

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {

                            if (!mViewPortHandler.isInBoundsRight(x))
                                break;

                            if (!mViewPortHandler.isInBoundsY(buffer.buffer[bufferIndex + 1])
                                    || !mViewPortHandler.isInBoundsLeft(x))
                                continue;

                            drawValue(c, dataSet.getValueFormatter(), entry.getY(), entry, i, x,
                                    buffer.buffer[bufferIndex + 1] + (entry.getY() >= 0 ? posOffset : negOffset),
                                    color);

                            // draw stack values
                        } else {

                            float[] transformed = new float[vals.length * 2];

                            float posY = 0f;
                            float negY = -entry.getNegativeSum();

                            for (int k = 0, idx = 0; k < transformed.length; k += 2, idx++) {

                                float value = vals[idx];
                                float y;

                                if (value >= 0f) {
                                    posY += value;
                                    y = posY;
                                } else {
                                    y = negY;
                                    negY -= value;
                                }

                                transformed[k + 1] = y * phaseY;
                            }

                            trans.pointValuesToPixel(transformed);

                            for (int k = 0; k < transformed.length; k += 2) {

                                float y = transformed[k + 1]
                                        + (vals[k / 2] >= 0 ? posOffset : negOffset);

                                if (!mViewPortHandler.isInBoundsRight(x))
                                    break;

                                if (!mViewPortHandler.isInBoundsY(y)
                                        || !mViewPortHandler.isInBoundsLeft(x))
                                    continue;

                                drawValue(c, dataSet.getValueFormatter(), vals[k / 2], entry, i, x, y, color);
                            }
                        }

                        bufferIndex = vals == null ? bufferIndex + 4 : bufferIndex + 4 * vals.length;
                        index++;
                    }
                }
            }
        }
    }

    private void scale(RectF rect, float scaleX, float scaleY) {
        float diffHorizontal = (rect.right-rect.left) * (scaleX-1f);
        float diffVertical = (rect.bottom-rect.top) * (scaleY-1f);

        rect.top -= diffVertical/2f;
        rect.bottom += diffVertical/2f;

        rect.left -= diffHorizontal/2f;
        rect.right += diffHorizontal/2f;
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        BarData barData = mChart.getBarData();

        // ====================================================================================
        // Daniel (2016-12-01 10:10:18): 차트를 그리기 전에 먼저 뒤에 배경부터 그려야 함...
        try {
            if (mChart.isDrawXGroupBackgroundWhenHighlighted()) {
                if (indices != null && indices.length > 0) {
//            // draw the grid background
                    Paint mGridBackgroundPaint = new Paint();
                    mGridBackgroundPaint.setStyle(Paint.Style.FILL);
                    // mGridBackgroundPaint.setColor(Color.WHITE);
                    mGridBackgroundPaint.setColor(Color.rgb(240, 240, 240)); // light
                    RectF rectF = mViewPortHandler.getContentRect();
//
                    IBarDataSet set = barData.getDataSetByIndex(indices[0].getDataSetIndex());

                    if (set != null) {
                        BarEntry e = set.getEntryForXValue(indices[0].getX(), indices[0].getY());

                        int index = set.getEntryIndex(e);
                        int xGroupCount = mChart.getXGroupFieldCountWhenHighlighted();
                        float xArea = (rectF.right - rectF.left) / xGroupCount;
                        DANIEL.log().d("index : " + index);
                        DANIEL.log().d("xArea : " + xArea);

                        float rightArea = xArea * (xGroupCount - (index + 1));  // 7은 x 그룹 수
                        float leftArea = xArea * index;  // 7은 x 그룹 수

                        // Scale 및 translation 부분 처리
                        RectF newRectForTrans = new RectF(
                                rectF.left + leftArea, rectF.top
                                , rectF.right - rightArea, rectF.bottom);

//                        scale(newRectForTrans, mViewPortHandler.getScaleX(), mViewPortHandler.getScaleY());

                        // Daniel (2016-12-02 11:00:35): 현재 highlight background 의 scale & translate 부분은
                        // 아직 해결이 안된 상태이다. TODO: 추후 좋은 방법이 생각나면 처리를 할 것
                        if (mViewPortHandler.getScaleX() == 1.0f && mViewPortHandler.getScaleY() == 1.0f
                                && mViewPortHandler.getTransX() == 0.0f && mViewPortHandler.getTransY() == 0.0f)
                            c.drawRect(newRectForTrans.left, newRectForTrans.top, newRectForTrans.right, newRectForTrans.bottom, mGridBackgroundPaint);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        // ===============================================================================================

        for (Highlight high : indices) {

            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;

            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(set.getHighLightAlpha());

            boolean isStack = (high.getStackIndex() >= 0  && e.isStacked()) ? true : false;

            final float y1;
            final float y2;

            if (isStack) {

                if(mChart.isHighlightFullBarEnabled()) {

                    y1 = e.getPositiveSum();
                    y2 = -e.getNegativeSum();

                } else {

                    Range range = e.getRanges()[high.getStackIndex()];

                    y1 = range.from;
                    y2 = range.to;
                }

            } else {
                y1 = e.getY();
                y2 = 0.f;
            }

            prepareBarHighlight(e.getX(), y1, y2, barData.getBarWidth() / 2f, trans);

            setHighlightDrawPos(high, mBarRect);

            if (mChart.isDrawBarTopRoundEnabled() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Daniel (2016-12-01 14:35:20): highlight 부분을 top-round 하게 처리함.
                c.drawRoundRect(mBarRect, 10f, 10f, mHighlightPaint);
            } else {
                c.drawRect(mBarRect, mHighlightPaint);
            }
        }
    }

    /**
     * Sets the drawing position of the highlight object based on the riven bar-rect.
     * @param high
     */
    protected void setHighlightDrawPos(Highlight high, RectF bar) {
        high.setDraw(bar.centerX(), bar.top);
    }

    @Override
    public void drawExtras(Canvas c) {
    }
}
