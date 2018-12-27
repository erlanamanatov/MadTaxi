package com.erkprog.madtaxi.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import static android.content.Context.LOCATION_SERVICE;

public class LocationHelper {

  private Context mContext;
  private LocationManager mLocationManager;

  public interface OnLocationChangedListener {
    void onLocationChanged(Location location);
  }

  public LocationHelper(Context context) {
    mContext = context;
    mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
  }

  @SuppressLint("MissingPermission")
  public void getLocation(final OnLocationChangedListener listener) {

    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new LocationListener() {
      @Override
      public void onLocationChanged(Location location) {
        listener.onLocationChanged(location);
        mLocationManager.removeUpdates(this);
      }

      @Override
      public void onStatusChanged(String provider, int status, Bundle extras) {

      }

      @Override
      public void onProviderEnabled(String provider) {

      }

      @Override
      public void onProviderDisabled(String provider) {

      }
    });
  }
}
