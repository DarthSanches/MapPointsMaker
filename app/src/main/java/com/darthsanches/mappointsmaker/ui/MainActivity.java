package com.darthsanches.mappointsmaker.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.darthsanches.mappointsmaker.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * Created by alexandroid on 2.08.16.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.placeholder, new LoginFragment(), "login").commit();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(getClass().getName(), "onMapReady");
    }
}
