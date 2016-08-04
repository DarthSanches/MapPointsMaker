package com.darthsanches.mappointsmaker.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import com.darthsanches.mappointsmaker.App;
import com.darthsanches.mappointsmaker.bus.LocationChangedEvent;
import com.darthsanches.mappointsmaker.bus.LoginEvent;
import com.darthsanches.mappointsmaker.bus.RequestPermissionEvent;
import com.darthsanches.mappointsmaker.ui.MainActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by alexandroid on 04.08.2016.
 */
public class LocationHelper {

    @Inject
    Bus bus;

    private Context context;

    LocationManager locationManager;
    LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            bus.post(new LocationChangedEvent(location));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    public LocationHelper(Context context) {
        this.context = context;
        App.component(context).inject(this);
        init();
    }

    public void init() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            bus.post(new RequestPermissionEvent());
            return;
        }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
    }

    public void checkLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            bus.post(new RequestPermissionEvent());
            return;
        }
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }

        bus.post(new LocationChangedEvent(bestLocation));
    }
}
