package com.darthsanches.mappointsmaker.bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;


/**
 * Created by alexandroid on 2.08.16.
 */
public class AndroidBus extends Bus {

    private final Handler mainThread = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(() -> AndroidBus.super.post(event));
        }
    }
}
