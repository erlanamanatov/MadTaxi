package com.erkprog.madtaxi.ui.main;

import android.Manifest;
//import android.app.FragmentManager;
import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.erkprog.madtaxi.R;
import com.erkprog.madtaxi.TaxiApplication;
import com.erkprog.madtaxi.data.entity.Company;
import com.erkprog.madtaxi.data.entity.TaxiCab;
import com.erkprog.madtaxi.util.MyUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, MainContract.View, OrderDialog.OnMakeOrderListener {

  private static final String TAG = "MainActivity";
  private static final int REQUEST_PHONE_CALL = 0;
  private static final int REQUEST_GPS = 1;
  private String pendingCall;


  private GoogleMap mMap;
  private MainContract.Presenter mPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    mPresenter = new MainPresenter(TaxiApplication.getInstance().getApiService());
    mPresenter.bind(this);

    findViewById(R.id.search).setOnClickListener(v -> {
      LatLng centerMap = mMap.getCameraPosition().target;
      mPresenter.loadData(centerMap.latitude, centerMap.longitude);
    });
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
    mMap.setOnInfoWindowClickListener(marker -> {

      TaxiCab taxiCab = (TaxiCab) marker.getTag();
      if (taxiCab != null) {
        OrderDialog dialog = OrderDialog.newInstance(taxiCab.getSmsNum(), taxiCab.getPhoneNum());
        dialog.show(getSupportFragmentManager(), "order dialog");
      }
    });
  }

  @Override
  public void displayTaxi(List<TaxiCab> taxiCabs) {
    mMap.clear();

    for (TaxiCab taxi : taxiCabs) {
      int logoRes = MyUtil.getLogo(taxi.getCompanyName());
      mMap.addMarker(new MarkerOptions().position(new LatLng(taxi.getLat(), taxi.getLng()))
          .icon(BitmapDescriptorFactory.fromResource(logoRes)))
          .setTag(taxi);
    }
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

  private boolean callPhonePermissionGranted() {
    return ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
  }

  private void requestCallPhonePermission() {
    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
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
      }
    }
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
}
