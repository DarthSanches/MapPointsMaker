package com.darthsanches.mappointsmaker.di;


import com.darthsanches.mappointsmaker.App;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by alexandroid on 2.08.16.
 */
@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface AppComponent {

    final class Initializer {
        public static AppComponent init(App app) {
            return DaggerAppComponent.builder()
                    .appModule(new AppModule(app))
                    .build();
        }
    }
}