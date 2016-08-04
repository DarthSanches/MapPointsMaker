package com.darthsanches.mappointsmaker.bus;

import com.darthsanches.mappointsmaker.model.Point;

import java.util.Collections;
import java.util.List;

/**
 * Created by alexandroid on 04.08.2016.
 */
public class PointsCommingEvent {

    final List<Point> points;

    public PointsCommingEvent(final List<Point> points) {
        this.points = Collections.unmodifiableList(points);
    }

    public List<Point> getPoints() {
        return points;
    }
}
