package com.erkprog.madtaxi.ui.main;

import com.erkprog.madtaxi.data.entity.TaxiCab;

import java.util.List;

public interface MainContract {

  interface View {

    void showMessage(String message);

    void displayTaxi(List<TaxiCab> taxiCabs);

    void showOrderDialog(TaxiCab taxiCab);
  }

  interface Presenter {

    void bind(View v);

    void unbind();

    boolean isViewAttached();

    void loadData(double lat, double lng);

    void onInfoWindowClicked(TaxiCab taxiCab);
  }
}
