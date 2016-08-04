package com.darthsanches.mappointsmaker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.darthsanches.mappointsmaker.App;
import com.darthsanches.mappointsmaker.R;
import com.darthsanches.mappointsmaker.bus.LocationChangedEvent;
import com.darthsanches.mappointsmaker.bus.PointsCommingEvent;
import com.darthsanches.mappointsmaker.model.Point;
import com.darthsanches.mappointsmaker.socket.SocketService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by alexandroid on 02.08.2016.
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static float ZOOM_LEVEL = 10.0f;

    @Inject
    Bus bus;

    private GoogleMap map;
    private LatLng lastLocation;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        App.component(getActivity()).inject(this);
        getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.toolbar_title_map);

        Button disconnectButton = new Button(getContext());
        disconnectButton.setText(R.string.quit_text);
        disconnectButton.setTextSize(getResources().getDimension(R.dimen.button_text_font_size));
        disconnectButton.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
        disconnectButton.bringToFront();
        int padding = (int) getResources().getDimension(R.dimen.base_margin);
        disconnectButton.setPadding(padding, padding, padding, padding);
        ((FrameLayout) view).addView(disconnectButton);
        disconnectButton.setOnClickListener(v -> onDisconnectClick());
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

    @Subscribe
    public void newPointsComming(PointsCommingEvent event) {
        if (map == null) return;
        map.clear();
        for (Point point : event.getPoints()) {
            map.addMarker(new MarkerOptions().position(new LatLng(point.lat, point.lon)));
        }

    }

    @Subscribe
    public void onLocationChanged(LocationChangedEvent event) {
        lastLocation = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
        if (map != null) {
            moveCamera();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        moveCamera();
    }

    private void moveCamera() {
        if (lastLocation != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, ZOOM_LEVEL));
        }
    }

    public void onDisconnectClick() {
        getActivity().stopService(new Intent(getActivity(), SocketService.class));
        ((MainActivity) getActivity()).openLoginFragment();
    }
}
