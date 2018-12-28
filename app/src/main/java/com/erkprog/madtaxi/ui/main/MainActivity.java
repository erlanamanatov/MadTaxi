package com.erkprog.madtaxi.ui.main;

import android.Manifest;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.erkprog.madtaxi.R;
import com.erkprog.madtaxi.TaxiApplication;
import com.erkprog.madtaxi.data.LocationHelper;
import com.erkprog.madtaxi.data.entity.TaxiCab;
import com.erkprog.madtaxi.util.MyUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, MainContract.View, OrderDialog.OnMakeOrderListener {

  private static final String TAG = "MainActivity";
  private static final int REQUEST_PHONE_CALL = 0;
  private static final int REQUEST_GPS = 1;
  private String pendingCall;


  private GoogleMap mMap;
  private MainContract.Presenter mPresenter;
  ImageView getLocationIcon;
  ProgressBar gpsProgressBar;
  TextView gpsInfoText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init();
    mPresenter = new MainPresenter(TaxiApplication.getInstance().getApiService(), new LocationHelper(this));
    mPresenter.bind(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    setUpGoogleMap();
    LatLng bishkek = new LatLng(42.88, 74.58);
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bishkek, 13));
    mPresenter.loadData(bishkek.latitude, bishkek.longitude);
  }

  private void setUpGoogleMap() {
    mMap.getUiSettings().setRotateGesturesEnabled(false);
    mMap.getUiSettings().setTiltGesturesEnabled(false);
    mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(this));
    mMap.setOnMarkerClickListener(marker -> {
      marker.showInfoWindow();
      return true;
    });
    mMap.setOnInfoWindowClickListener(marker -> {
      TaxiCab taxiCab = (TaxiCab) marker.getTag();
      if (taxiCab != null) {
        mPresenter.onInfoWindowClicked(taxiCab);
      }
    });
  }

  @Override
  public void displayNearistTaxiCabs(List<TaxiCab> taxiCabs) {
    mMap.clear();
    for (TaxiCab taxi : taxiCabs) {
      int logoRes = MyUtil.getLogo(taxi.getCompanyName());
      mMap.addMarker(new MarkerOptions().position(new LatLng(taxi.getLat(), taxi.getLng()))
          .icon(BitmapDescriptorFactory.fromResource(logoRes)))
          .setTag(taxi);
    }
  }

  @Override
  public void centerMapToLocation(Location location) {
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
  }

  @Override
  public void onGettingLocation() {
    getLocationIcon.setVisibility(View.INVISIBLE);
    getLocationIcon.setEnabled(false);
    gpsProgressBar.setVisibility(View.VISIBLE);
    gpsInfoText.setVisibility(View.VISIBLE);
  }

  @Override
  public void setIconsDefaultState() {
    getLocationIcon.setVisibility(View.VISIBLE);
    getLocationIcon.setEnabled(true);
    gpsProgressBar.setVisibility(View.GONE);
    gpsInfoText.setVisibility(View.GONE);
  }

  @Override
  public void showOrderDialog(TaxiCab taxiCab) {
    OrderDialog dialog = OrderDialog.newInstance(taxiCab.getSmsNum(), taxiCab.getPhoneNum());
    dialog.show(getSupportFragmentManager(), "order dialog");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mPresenter.unbind();
  }

  @Override
  public void showMessage(String message) {
    Snackbar.make(findViewById(R.id.map), message, Snackbar.LENGTH_LONG).show();
  }


  @Override
  public void makeAnOrderUsingSms(String smsNum) {
    Uri uriSms = Uri.parse("smsto:" + smsNum);
    Intent intentSMS = new Intent(Intent.ACTION_SENDTO, uriSms);
    intentSMS.putExtra("sms_body", "");
    if (intentSMS.resolveActivity(getPackageManager()) != null) {
      startActivity(intentSMS);
    }
  }

  @Override
  public void makeAnOrderUsingPhoneCall(String phoneNumber) {
    if (callPhonePermissionGranted()) {
      Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
      if (intent.resolveActivity(getPackageManager()) != null) {
        startActivity(intent);
      }
    } else {
      pendingCall = phoneNumber;
      requestCallPhonePermission();
    }
  }

  private boolean callPhonePermissionGranted() {
    return ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
  }

  private void requestCallPhonePermission() {
    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
  }

  private boolean isGpsEnabled() {
    LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  private boolean isGpsPersmissionGranted() {
    return ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
  }

  private void requestGpsPermission() {
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
  }

  private void showTurnGpsOnDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setMessage("Turn on gps in settings")
        .setTitle("Gps is disabled")
        .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(MainActivity.this, "Turn GPS on to get forecast for current location", Toast.LENGTH_SHORT).show();
          }
        });
    builder.show();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_PHONE_CALL) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        if (pendingCall != null) {
          Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + pendingCall));
          pendingCall = null;
          startActivity(intent);
        }
      } else if (pendingCall != null) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + pendingCall));
        pendingCall = null;
        startActivity(callIntent);
      }
    }

    if (requestCode == REQUEST_GPS) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        mPresenter.getCurrentLocation();
      } else {
        Toast.makeText(this, "Location Permission denied", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void init() {
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    getLocationIcon = findViewById(R.id.get_location_img);
    getLocationIcon.setOnClickListener(v -> {
      if (isGpsPersmissionGranted()) {
        if (isGpsEnabled()) {
          mPresenter.getCurrentLocation();
        } else {
          showTurnGpsOnDialog();
        }
      } else {
        requestGpsPermission();
      }
    });

    gpsProgressBar = findViewById(R.id.main_gps_progress);
    gpsInfoText = findViewById(R.id.main_gps_textinfo);
    setIconsDefaultState();

    findViewById(R.id.search).setOnClickListener(v -> {
      LatLng centerMap = mMap.getCameraPosition().target;
      mPresenter.loadData(centerMap.latitude, centerMap.longitude);
    });
  }
}
