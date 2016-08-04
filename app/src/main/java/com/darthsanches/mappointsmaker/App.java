package com.darthsanches.mappointsmaker;

import android.app.Application;
import android.content.Context;

import com.darthsanches.mappointsmaker.di.AppComponent;

/**
 * Created by alexandroid on 2.08.16.
 */
public class App extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = AppComponent.Initializer.init(this);
    }

    public static AppComponent component(Context context) {
        return ((App) context.getApplicationContext()).component;
    }
}
