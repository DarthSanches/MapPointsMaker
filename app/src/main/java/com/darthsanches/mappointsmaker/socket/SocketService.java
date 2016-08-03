package com.darthsanches.mappointsmaker.socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.darthsanches.mappointsmaker.App;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.squareup.otto.Bus;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;
import okio.BufferedSink;

/**
 * Created by alexandroid on 2.08.16.
 */
public class SocketService extends Service implements WebSocketListener, OnMapReadyCallback {

    private WebSocket webSocket;
    private IBinder binder;

    @Inject
    @Named("SocketHttpClient")
    OkHttpClient okHttp;
    @Inject
    Bus bus;
    @Inject
    Handler handler;

    private String username = "a";
    private String password = "a";

    @Override
    public IBinder onBind(Intent intent) {
        return binder == null ? binder = new Binder() : binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(getClass().getName(), "SOCKET CREATE");
        App.component(this).inject(this);
        createSocketCall().enqueue(this);
        bus.register(this);
    }

/*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.i(getClass().getName(), "pam");
                }
            }
        }).start();
        return 0;
    }
*/




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
        try {
            webSocket.sendMessage(RequestBody.create(WebSocket.TEXT, "{ \"lat\": 55.373703, \"lon\": 37.474764}"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(IOException e, Response response) {
        this.webSocket = null;//TODO backoff reconnect
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
        Log.i(getClass().getName(), message.string());
    }


    @Override
    public void onPong(Buffer payload) {
    }

    @Override
    public void onClose(int code, String reason) {
        this.webSocket = null;
        Log.d(getClass().getName(), "CLOSE");
    }

    public void onActivityStopped() {
        if (webSocket != null) {
            try {
                webSocket.close(0, null);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(getClass().getName(), e.toString());
            }
        }
    }

    private WebSocketCall createSocketCall() {
        String url = "ws://mini-mdt.wheely.com" + "/?username=" + username + "&password=" + password;
        return WebSocketCall.create(okHttp, new Request.Builder().url(url).build());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(getClass().getName(), "zbs magic");
    }
}
