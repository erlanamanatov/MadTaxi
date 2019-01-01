package com.erkprog.madtaxi.ui.main;

import android.location.Location;

import com.erkprog.madtaxi.data.entity.TaxiCab;
import com.erkprog.madtaxi.data.entity.TaxiClusterItem;

import java.util.List;

public interface MainContract {

  interface View {

    void showMessage(String message);

    void displayNearistTaxiCabs(List<TaxiClusterItem> taxiItems);

    void showOrderDialog(TaxiCab taxiCab);

    void centerMapToLocation(Location location);

    void onGettingLocation();

    void setIconsDefaultState();

    void showAddress(String s);
  }

  interface Presenter {

    void bind(View v);

    void unbind();

    boolean isViewAttached();

    void loadData(double lat, double lng);

    void onInfoWindowClicked(TaxiCab taxiCab);

    void getCurrentLocation();

    void getAddress(double lat, double lng);

    void onMapReady();
  }
}
