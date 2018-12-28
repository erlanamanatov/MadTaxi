package com.erkprog.madtaxi.ui.main;

import android.location.Location;

import com.erkprog.madtaxi.data.entity.TaxiCab;

import java.util.List;

public interface MainContract {

  interface View {

    void showMessage(String message);

    void displayNearistTaxiCabs(List<TaxiCab> taxiCabs);

    void showOrderDialog(TaxiCab taxiCab);

    void centerMapToLocation(Location location);

    void onGettingLocation();

    void setIconsDefaultState();

  }

  interface Presenter {

    void bind(View v);

    void unbind();

    boolean isViewAttached();

    void loadData(double lat, double lng);

    void onInfoWindowClicked(TaxiCab taxiCab);

    void getCurrentLocation();
  }
}
