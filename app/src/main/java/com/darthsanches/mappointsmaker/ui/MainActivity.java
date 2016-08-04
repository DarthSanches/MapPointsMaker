package com.darthsanches.mappointsmaker.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.darthsanches.mappointsmaker.R;
import com.darthsanches.mappointsmaker.socket.SocketService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.security.Permission;

/**
 * Created by alexandroid on 2.08.16.
 */
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            if (!isMyServiceRunning(SocketService.class)) {
                openMapFragment();
            } else {
                openLoginFragment();
            }
        }
    }

    public void openLoginFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.placeholder, new LoginFragment(), "login").commit();
    }

    public void openMapFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.placeholder, new MapFragment(), "map").commit();
    }

    public void requestMultiplePermissions() {
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
