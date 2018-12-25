package com.erkprog.madtaxi;

import android.app.Application;

import com.erkprog.madtaxi.data.api.ApiClient;
import com.erkprog.madtaxi.data.api.TaxiApi;

public class TaxiApplication extends Application {

  private static TaxiApplication instance;

  private TaxiApi mTaxiApi;

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    mTaxiApi = ApiClient.getClient(this);
  }

  public static TaxiApplication getInstance() {
    return  instance;
  }

  public TaxiApi getApiService(){
    return mTaxiApi;
  }

}
