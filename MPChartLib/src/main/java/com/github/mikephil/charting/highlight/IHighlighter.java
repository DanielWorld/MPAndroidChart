package com.github.mikephil.charting.highlight;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipp on 10/06/16.
 */
public interface IHighlighter
{

    /**
     * Returns a Highlight object corresponding to the given x- and y- touch positions in pixels.
     *
     * @param x
     * @param y
     * @return
     */
    Highlight getHighlight(float x, float y);

    /**
     * 주어진 entry x 값에 대한 highlight 가져올 것
     * @param x
     * @return
     */
    List<Highlight> getHighlight(int xVal, float x, float y);
}
