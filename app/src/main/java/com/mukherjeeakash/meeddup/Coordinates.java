package com.mukherjeeakash.meeddup;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akash on 11/22/2017.
 */

public class Coordinates {
    private double lat;
    private double lng;

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public List<Double> getCoordinates() {
        List<Double> coordinates = new ArrayList<Double>();
        coordinates.add(lat);
        coordinates.add(lng);
        return coordinates;
    }
}
