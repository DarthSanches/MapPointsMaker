package com.darthsanches.mappointsmaker.bus;

import android.location.Location;

/**
 * Created by alexandroid on 04.08.2016.
 */
public class LocationChangedEvent {

    private final Location location;

    public LocationChangedEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
