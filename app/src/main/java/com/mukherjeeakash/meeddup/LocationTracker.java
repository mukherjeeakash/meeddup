package com.mukherjeeakash.meeddup;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by akash on 11/13/2017.
 * <p>
 * Tracks changes in user location and returns user's location when requested
 */

public class LocationTracker implements LocationListener {
    private Context context;
    private static Toast toast;

    public LocationTracker(Context context) {
        this.context = context;
    }

    /**
     * Returns the user's last known location
     *
     * @return Location current location object
     */
    public Location getLocation() {
        final int MIN_TIME = 0;
        final int MIN_DIST = 0;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            createToast("GPS Permission Needed");
            return null;
        }

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, this);
            Location gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (gpsLocation == null) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST,
                        this);
                return lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            return gpsLocation;
        } else {
            createToast("Enable GPS to Continue");
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //overridden method declaration required but no implementation
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //overridden method declaration required but no implementation
    }

    @Override
    public void onProviderEnabled(String provider) {
        //overridden method declaration required but no implementation
    }

    @Override
    public void onProviderDisabled(String provider) {
        //overridden method declaration required but no implementation
    }

    public void createToast(String text) {
        // https://stackoverflow.com/questions/6925156/how-to-avoid-a-toast-if-theres-one-toast
        // -already-being-shown
        try {
            toast.getView().isShown();     // true if visible
            toast.setText(text);
        } catch (Exception e) {         // invisible if exception
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }

        toast.show();
    }
}
