package com.erkprog.madtaxi.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.erkprog.madtaxi.util.MyUtil;

import java.io.IOException;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class LocationHelper {
  private static final String TAG = "LocationHelper";

  private Context mContext;
  private LocationManager mLocationManager;
  private Geocoder mGeocoder;

  public interface OnLocationChangedListener {
    void onLocationChanged(Location location);
  }

  public LocationHelper(Context context) {
    mContext = context;
    mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
    mGeocoder = new Geocoder(mContext);
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

  public String getAddress(double lat, double lon) throws IOException {
    if (mGeocoder.isPresent()) {
      List<Address> list = mGeocoder.getFromLocation(lat, lon ,1);
      Address address = list.get(0);

      StringBuffer str = new StringBuffer();
//      str.append("Name: " + address.getLocality() + "\n");
//      str.append("Sub-Admin Areas: " + address.getSubAdminArea() + "\n");
//      str.append("Admin area: " + address.getAdminArea() + "\n");

      for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
        str.append(address.getAddressLine(i)).append("\n");
      }
      return str.toString();
    } else {
      return "";
    }
  }
}
