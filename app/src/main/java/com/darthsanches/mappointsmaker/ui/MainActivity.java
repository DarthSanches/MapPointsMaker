package com.darthsanches.mappointsmaker.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.darthsanches.mappointsmaker.App;
import com.darthsanches.mappointsmaker.R;
import com.darthsanches.mappointsmaker.bus.LocationChangedEvent;
import com.darthsanches.mappointsmaker.bus.LoginEvent;
import com.darthsanches.mappointsmaker.bus.RequestPermissionEvent;
import com.darthsanches.mappointsmaker.helper.LocationHelper;
import com.darthsanches.mappointsmaker.socket.SocketService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.security.Permission;

import javax.inject.Inject;

/**
 * Created by alexandroid on 2.08.16.
 */
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Inject
    Bus bus;

    LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.component(this).inject(this);

        locationHelper = new LocationHelper(this);

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

    @Subscribe
    public void onLogin(LoginEvent event) {
        locationHelper.checkLastLocation();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHelper.init();
    }
}
