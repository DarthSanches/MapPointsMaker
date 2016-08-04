package com.darthsanches.mappointsmaker.di;

import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by alexandroid on 2.08.16.
 */
@Module
public class ApiModule {

    @Provides
    @Singleton
    @Named("SocketHttpClient")
    OkHttpClient provideSocketHttpClient(Context context) {
        return new OkHttpClient();
    }
}
