package com.darthsanches.mappointsmaker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by alexandroid on 02.08.2016.
 */
public class MapFragment extends SupportMapFragment {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getMapAsync((MainActivity)getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {

        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

}
