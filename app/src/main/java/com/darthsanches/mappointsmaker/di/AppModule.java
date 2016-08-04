package com.darthsanches.mappointsmaker.di;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.darthsanches.mappointsmaker.App;
import com.darthsanches.mappointsmaker.bus.AndroidBus;
import com.darthsanches.mappointsmaker.helper.LocationHelper;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/**
 * Created by alexandroid on 2.08.16.
 */
@Module
public class AppModule {

    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    Bus provideBus() {
        return new AndroidBus();
    }

    @Provides
    @Singleton
    Handler provideHandler() {
        return new Handler(Looper.getMainLooper());
    }
}
