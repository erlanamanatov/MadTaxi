package com.erkprog.madtaxi.ui.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.erkprog.madtaxi.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

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

    mPresenter = new MainPresenter();
    mPresenter.bind(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    LatLng bishkek = new LatLng(42.88, 74.58);
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bishkek, 13));
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    mPresenter.unbind();
  }
}
