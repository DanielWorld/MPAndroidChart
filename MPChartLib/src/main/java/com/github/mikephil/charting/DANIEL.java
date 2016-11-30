package com.github.mikephil.charting;

import android.util.Log;

/**
 * Copyright (C) 2014-2016 daniel@bapul.net
 * Created by Daniel on 2016-11-29.
 */

public class DANIEL {
    private static DANIEL sThis;

    public DANIEL() {}	// Daniel (2016-08-12 16:10:22): singleton 방식 구현

    public synchronized static final DANIEL log() {
        if (sThis == null)
            sThis = new DANIEL();

        return sThis;
    }

    private final String TAG = "DANIEL";
    private boolean mLogFlag = true;

    public void v(String msg) {
        if (mLogFlag) {
            Log.v(TAG, "" + msg);
        }
    }

    public void d(String msg) {
        if (mLogFlag) {
            Log.d(TAG, "" + msg);
        }
    }

    public void e(String msg) {
        if (mLogFlag) {
            Log.e(TAG, "" + msg);
        }
    }

    public void i(String msg) {
        if (mLogFlag) {
            Log.i(TAG, "" + msg);
        }
    }

    public void w(String msg) {
        if (mLogFlag) {
            Log.w(TAG, "" + msg);
        }
    }

    public void wtf(String msg) {
        if (mLogFlag) {
            Log.wtf(TAG, "" + msg);
        }
    }
}
