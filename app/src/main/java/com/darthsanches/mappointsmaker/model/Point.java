package com.darthsanches.mappointsmaker.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alexandroid on 04.08.2016.
 */
public class Point {
    @SerializedName("id")
    public int id;
    @SerializedName("lat")
    public double lat;
    @SerializedName("lon")
    public double lon;
}
