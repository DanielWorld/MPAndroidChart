package com.github.mikephil.charting.interfaces.dataprovider;

import android.annotation.TargetApi;
import android.os.Build;

import com.github.mikephil.charting.data.BarData;

public interface BarDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BarData getBarData();
    boolean isDrawBarShadowEnabled();
    boolean isDrawValueAboveBarEnabled();
    boolean isHighlightFullBarEnabled();

    /**
     * 차트를 그릴 때, top 부분 round 처리 여부
     * <p>기본값은 false 이며, 롤리팝 미만 버전에서는 적용되지 않는다.</p>
     * @return
     */
    boolean isDrawBarTopRoundEnabled();

    /**
     * {@link BarDataProvider#isDrawBarTopRoundEnabled()} true 일 경우, top 부분이 round 하게 그려지는데
     * 그 때 radius 값
     * @return
     */
    float getDrawBarTopRoundRadius();
}
