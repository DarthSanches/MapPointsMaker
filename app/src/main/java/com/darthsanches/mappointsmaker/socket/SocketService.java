package com.darthsanches.mappointsmaker.socket;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.darthsanches.mappointsmaker.App;
import com.darthsanches.mappointsmaker.bus.LocationChangedEvent;
import com.darthsanches.mappointsmaker.bus.PointsCommingEvent;
import com.darthsanches.mappointsmaker.model.LocationRequest;
import com.darthsanches.mappointsmaker.model.Point;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;

/**
 * Created by alexandroid on 2.08.16.
 */
public class SocketService extends Service implements WebSocketListener {

    @Inject
    @Named("SocketHttpClient")
    OkHttpClient okHttp;
    @Inject
    Bus bus;
    @Inject
    Handler handler;

    private WebSocket webSocket;
    private IBinder binder;
    private Gson gson;

    private String username = "a";
    private String password = "a";

    @Override
    public IBinder onBind(Intent intent) {
        return binder == null ? binder = new Binder() : binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(getClass().getName(), "SOCKET CREATE");
        App.component(this).inject(this);
        createSocketCall().enqueue(this);
        bus.register(this);
        gson = new Gson();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    public void sendLocation(Location location){
        if(webSocket != null && location != null){
            try {
                LocationRequest request= new LocationRequest(location.getLatitude(), location.getLongitude());
                webSocket.sendMessage(RequestBody.create(WebSocket.TEXT,gson.toJson(request)));
                Log.i(getClass().getName(), "send: " + location.getLongitude() + ", " + location.getLatitude());
                bus.post(new LocationChangedEvent(location));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Thread thread = new Thread(() -> {
            try {
                if (webSocket != null) {
                    webSocket.close(0, null);
                    webSocket = null;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        thread.start();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.webSocket = webSocket;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }else {
            sendLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        }
    }

    @Override
    public void onFailure(IOException e, Response response) {
        this.webSocket = null;
        response.code();
        handler.postDelayed(() -> createSocketCall().enqueue(SocketService.this), 1000);
        try {
            if (response != null) {
                Log.w(getClass().getName(), String.format("FAILURE RESPONSE:\n %s", response.body() != null ? response.body().string() : response.body()));
            }
        } catch (IOException e1) {
            Log.w("", "FAILURE WHEN GETTING RESPONSE BODY", e);
        }

    }

    @Override
    public void onMessage(ResponseBody message) throws IOException {
        Point[] points = gson.fromJson(message.string(), Point[].class);
        bus.post(new PointsCommingEvent(Arrays.asList(points)));
    }


    @Override
    public void onPong(Buffer payload) {
    }

    @Override
    public void onClose(int code, String reason) {
        this.webSocket = null;
        Log.d(getClass().getName(), "CLOSE");
    }

    private WebSocketCall createSocketCall() {
        String url = "ws://mini-mdt.wheely.com" + "/?username=" + username + "&password=" + password;
        return WebSocketCall.create(okHttp, new Request.Builder().url(url).build());
    }

    LocationManager locationManager;

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            sendLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

}
