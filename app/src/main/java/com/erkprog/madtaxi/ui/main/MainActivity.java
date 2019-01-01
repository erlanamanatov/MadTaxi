package com.erkprog.madtaxi.ui.main;

import android.Manifest;
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
import com.erkprog.madtaxi.data.entity.TaxiClusterItem;
import com.erkprog.madtaxi.util.MyUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, MainContract.View, OrderDialog.OnMakeOrderListener {

  private static final String TAG = "MainActivity";
  private static final int REQUEST_PHONE_CALL = 0;
  private static final int REQUEST_GPS = 1;
  private static final String CURRENT_LAT = "current lat";
  private static final String CURRENT_LNG = "current lng";
  private static final String CURRENT_ZOOM = "current zoom";
  private String pendingCall;
  private GoogleMap mMap;
  private MainContract.Presenter mPresenter;
  ImageView getLocationIcon;
  ProgressBar gpsProgressBar;
  TextView gpsInfoText, tvAddress;
  LatLng currentLocation;
  Float currentZoom;
  private ClusterManager<TaxiClusterItem> mClusterManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    attachPresenter();
    init();

    if (savedInstanceState != null) {
      currentLocation = new LatLng(
          savedInstanceState.getDouble(CURRENT_LAT, 42.88),
          savedInstanceState.getDouble(CURRENT_LNG, 74.58)
      );
      currentZoom = savedInstanceState.getFloat(CURRENT_ZOOM, 14);
    }
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  private void init() {
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
    tvAddress = findViewById(R.id.address);
    onLocationFound();

    findViewById(R.id.search).setOnClickListener(v -> {
      LatLng centerMap = mMap.getCameraPosition().target;
      mPresenter.loadData(centerMap.latitude, centerMap.longitude);
    });
  }

  private void attachPresenter() {
    mPresenter = (MainContract.Presenter) getLastCustomNonConfigurationInstance();
    if (mPresenter == null) {
      mPresenter = new MainPresenter(TaxiApplication.getInstance().getApiService(), new LocationHelper(this));
    }
    mPresenter.bind(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    setUpGoogleMap();
    mPresenter.onMapReady();
  }

  private void setUpGoogleMap() {
    mMap.getUiSettings().setRotateGesturesEnabled(false);
    mMap.getUiSettings().setTiltGesturesEnabled(false);
    mMap.setOnInfoWindowClickListener(marker -> {
      TaxiCab taxiCab = (TaxiCab) marker.getTag();
      if (taxiCab != null) {
        mPresenter.onInfoWindowClicked(taxiCab);
      }
    });

    mClusterManager = new ClusterManager<>(this, mMap);
    mClusterManager.setRenderer(new TaxiRenderer());
    mMap.setOnCameraIdleListener(() -> {
      currentLocation = mMap.getCameraPosition().target;
      mPresenter.getAddress(currentLocation.latitude, currentLocation.longitude);
      mClusterManager.onCameraIdle();
    });
    mMap.setOnCameraMoveStartedListener(i -> tvAddress.setText(""));
    mMap.setOnMarkerClickListener(mClusterManager);
    mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
    mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MarkerInfoWindowAdapter(this));

    if (currentLocation == null) {
      // Acitivity created, savedInstance is null
      LatLng bishkek = new LatLng(42.88, 74.58);
      currentLocation = bishkek;
      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bishkek, 14));
      mPresenter.loadData(bishkek.latitude, bishkek.longitude);
    } else {
      // On screen rotation, location and zoom from savedInstance
      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, currentZoom));
    }
  }

  @Override
  public void displayNearistTaxiCabs(List<TaxiClusterItem> taxiItems) {
    mMap.clear();
    mClusterManager.clearItems();
    mClusterManager.addItems(taxiItems);
    mClusterManager.cluster();
  }

  @Override
  public void centerMapToLocation(Location location) {
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
  }

  @Override
  public void onGettingLocation() {
    getLocationIcon.setVisibility(View.INVISIBLE);
    getLocationIcon.setEnabled(false);
    gpsProgressBar.setVisibility(View.VISIBLE);
    gpsInfoText.setVisibility(View.VISIBLE);
  }

  @Override
  public void onLocationFound() {
    getLocationIcon.setVisibility(View.VISIBLE);
    getLocationIcon.setEnabled(true);
    gpsProgressBar.setVisibility(View.GONE);
    gpsInfoText.setVisibility(View.GONE);
  }

  @Override
  public void showAddress(String address) {
    tvAddress.setText(address);
  }

  @Override
  public void showOrderDialog(TaxiCab taxiCab) {
    OrderDialog dialog = OrderDialog.newInstance(taxiCab.getSmsNum(), taxiCab.getPhoneNum());
    dialog.show(getSupportFragmentManager(), "order dialog");
  }

  @Override
  public void showMessage(int resId) {
    Snackbar.make(findViewById(R.id.map), getString(resId), Snackbar.LENGTH_LONG).show();
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
        .setPositiveButton("Go to settings", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
        .setNegativeButton("Cancel", (dialog, which) -> Toast.makeText(MainActivity.this, "Turn GPS on to get forecast for current location", Toast.LENGTH_SHORT).show());
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

  @Override
  public Object onRetainCustomNonConfigurationInstance() {
    return mPresenter;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (currentLocation != null) {
      outState.putDouble(CURRENT_LAT, currentLocation.latitude);
      outState.putDouble(CURRENT_LNG, currentLocation.longitude);
      outState.putFloat(CURRENT_ZOOM, mMap.getCameraPosition().zoom);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mPresenter.unbind();
  }

  private class TaxiRenderer extends DefaultClusterRenderer<TaxiClusterItem> {

    TaxiRenderer() {
      super(getApplicationContext(), mMap, mClusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(TaxiClusterItem item, MarkerOptions markerOptions) {
      int logoRes = MyUtil.getLogo(item.getTaxiCab().getCompanyName());
      markerOptions.icon(BitmapDescriptorFactory.fromResource(logoRes));
    }

    @Override
    protected void onClusterItemRendered(TaxiClusterItem clusterItem, Marker marker) {
      super.onClusterItemRendered(clusterItem, marker);
      marker.setTag(clusterItem.getTaxiCab());
    }
  }
}
