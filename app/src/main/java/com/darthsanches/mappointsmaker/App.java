package com.darthsanches.mappointsmaker;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.darthsanches.mappointsmaker.di.AppComponent;
import com.darthsanches.mappointsmaker.socket.SocketService;

/**
 * Created by alexandroid on 2.08.16.
 */
public class App extends Application implements ServiceConnection {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = AppComponent.Initializer.init(this);
    }

    public static AppComponent component(Context context) {
        return ((App) context.getApplicationContext()).component;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d(getClass().getName(),"SocketService binded");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d(getClass().getName(), "SocketService disconnected");
    }

    public void unbindService() {
        try {
            unbindService(this);
        } catch (IllegalArgumentException e) {
            Log.e(getClass().getName(), "ERROR UNBINDING SOCKET", e);
        }
    }

    public void bindService() {
        bindService(new Intent(this, SocketService.class), this, BIND_IMPORTANT | BIND_AUTO_CREATE);
    }
}
