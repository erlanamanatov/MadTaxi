package com.erkprog.madtaxi.ui.main;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.erkprog.madtaxi.R;
import com.erkprog.madtaxi.TaxiApplication;
import com.erkprog.madtaxi.data.entity.Company;
import com.erkprog.madtaxi.data.entity.TaxiCab;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, MainContract.View {

  private static final String TAG = "MainActivity";

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
    mMap.getUiSettings().setRotateGesturesEnabled(false);
    mMap.getUiSettings().setTiltGesturesEnabled(false);
    mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(this));

    LatLng bishkek = new LatLng(42.88, 74.58);
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bishkek, 13));
    mPresenter.loadData(bishkek.latitude, bishkek.longitude);
  }

  @Override
  public void displayTaxi(List<TaxiCab> taxiCabs) {
    mMap.clear();
    for (TaxiCab taxi : taxiCabs) {
      int logoRes = R.drawable.car;
      switch (taxi.getCompanyName()) {
        case Company.COMPANY_NAMBA:
          logoRes = R.drawable.car_namba;
          break;
        case Company.COMPANY_SMS:
          logoRes = R.drawable.car_sms;
          break;
      }
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
}
