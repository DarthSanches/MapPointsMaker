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

//    @Provides
//    @Singleton
//    RestAdapter provideApiService(@Named("unsafe") final OkHttpClient okHttpClient, final Bus bus, RequestInterceptor interceptor, @Named("api") Endpoint endpoint, final PersistentUserInfoStore persistentUserInfoStore) {
//        okHttpClient.interceptors().add(chain -> {
//            Response response = chain.proceed(chain.request());
//            String authToken = response.header("X-Auth-Token");
//            if (!TextUtils.isEmpty(authToken)) {
//                persistentUserInfoStore.saveAuthToken(authToken);
//            }
//
//            if (response.code() == 401) {
//                bus.post(new NotAuthorizedEvent());
//                return null;
//            }
//
//            return response;
//        });
//        return new RestAdapter.Builder()
//                .setEndpoint(endpoint)
//                .setClient(new OkClient(okHttpClient))
//                .setRequestInterceptor(interceptor)
//                .setConverter(new GsonConverter(JsonUtils.provideGson()))
//                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
//                .build();
//    }

    @Provides
    @Singleton
    @Named("SocketHttpClient")
    OkHttpClient provideSocketHttpClient(Context context) {
        return new OkHttpClient();
    }
}
