package com.darthsanches.mappointsmaker.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.darthsanches.mappointsmaker.App;
import com.darthsanches.mappointsmaker.R;
import com.darthsanches.mappointsmaker.bus.RequestPermissionEvent;
import com.darthsanches.mappointsmaker.socket.SocketService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by alexandroid on 2.08.16.
 */
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Inject
    Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.component(this).inject(this);

        if (savedInstanceState == null) {
            if (isMyServiceRunning(SocketService.class)) {
                openMapFragment();
            } else {
                openLoginFragment();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    public void openLoginFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.placeholder, new LoginFragment(), "login").commit();
    }

    public void openMapFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.placeholder, new MapFragment(), "map").commit();
    }

    @Subscribe
    public void requestMultiplePermissions(RequestPermissionEvent event) {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                PERMISSION_REQUEST_CODE);
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
