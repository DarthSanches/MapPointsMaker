package com.darthsanches.mappointsmaker.socket;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.darthsanches.mappointsmaker.App;
import com.darthsanches.mappointsmaker.R;
import com.darthsanches.mappointsmaker.bus.LocationChangedEvent;
import com.darthsanches.mappointsmaker.bus.LoginEvent;
import com.darthsanches.mappointsmaker.bus.LoginFailureEvent;
import com.darthsanches.mappointsmaker.bus.PointsCommingEvent;
import com.darthsanches.mappointsmaker.helper.LocationHelper;
import com.darthsanches.mappointsmaker.model.LocationRequest;
import com.darthsanches.mappointsmaker.model.Point;
import com.darthsanches.mappointsmaker.ui.MainActivity;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    private static final int ONGOING_NOTIFICATION_ID = 1;

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

    private String username;
    private String password;
    private Location lastLocation;

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
        bus.register(this);
        gson = new Gson();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        createSocketCall().enqueue(this);

        startForeground(ONGOING_NOTIFICATION_ID, getNotification());
        return START_REDELIVER_INTENT;
    }

    public void sendLocation(Location location) {
        lastLocation = location;
        if (webSocket != null && location != null) {
            try {
                LocationRequest request = new LocationRequest(location.getLatitude(), location.getLongitude());
                webSocket.sendMessage(RequestBody.create(WebSocket.TEXT, gson.toJson(request)));
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
        bus.unregister(this);
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
        bus.post(new LoginEvent());
    }

    @Override
    public void onFailure(IOException e, Response response) {
        this.webSocket = null;
        if (response != null && response.code() == 403) {
            stopSelf();
            bus.post(new LoginFailureEvent());
        } else {
            handler.postDelayed(() -> createSocketCall().enqueue(SocketService.this), 1000);
            try {
                Log.w(getClass().getName(), String.format("FAILURE RESPONSE:\n %s", response.body() != null ? response.body().string() : response.body()));
            } catch (IOException e1) {
                Log.w("", "FAILURE WHEN GETTING RESPONSE BODY", e);
            }
        }

    }

    @Override
    public void onMessage(ResponseBody message) throws IOException {
        Point[] points = gson.fromJson(message.string(), Point[].class);
        bus.post(new PointsCommingEvent(Arrays.asList(points)));
        Log.i(getClass().getName(), "onMessage");
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

    private Notification getNotification(){
        Notification notification;

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification(R.mipmap.ic_launcher, getText(R.string.notification_text),
                    System.currentTimeMillis());

            try {
                Method deprecatedMethod = notification.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                deprecatedMethod.invoke(this, getText(R.string.notification_text), getText(R.string.notification_text), pendingIntent);
            } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                Log.w(getClass().getName(), "Method not found", e);
            }
        } else {
            Notification.Builder builder = new Notification.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getText(R.string.notification_text));
            notification = builder.build();
        }
        return notification;
    }

    @Subscribe
    public void onLocationChange(LocationChangedEvent event){
        sendLocation(event.getLocation());
    }
}
