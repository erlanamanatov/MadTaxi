package com.erkprog.madtaxi.data.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class TaxiClusterItem implements ClusterItem {

  private TaxiCab mTaxiCab;

  public TaxiClusterItem (TaxiCab taxiCab) {
    mTaxiCab = taxiCab;
  }

  @Override
  public LatLng getPosition() {
    return new LatLng(mTaxiCab.getLat(), mTaxiCab.getLng());
  }

  @Override
  public String getTitle() {
    return mTaxiCab.getCompanyName();
  }

  @Override
  public String getSnippet() {
    return null;
  }

  public TaxiCab getTaxiCab() {
    return mTaxiCab;
  }

  public void setTaxiCab(TaxiCab taxiCab) {
    mTaxiCab = taxiCab;
  }
}
