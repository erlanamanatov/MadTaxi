package com.erkprog.madtaxi.ui.main;

import android.util.Log;

import com.erkprog.madtaxi.data.api.TaxiApi;
import com.erkprog.madtaxi.data.entity.TaxiResponse;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPresenter implements MainContract.Presenter {

  private static final String TAG = "MainPresenter";

  private MainContract.View mView;
  private TaxiApi mApiService;


  MainPresenter(TaxiApi apiService) {
    mApiService = apiService;
  }

  @Override
  public void loadData() {
    if (mApiService != null) {
      mApiService.getNearistTaxi(42.882004, 74.582748).enqueue(new Callback<TaxiResponse>() {
        @Override
        public void onResponse(Call<TaxiResponse> call, Response<TaxiResponse> response) {
          if (isViewAttached()) {
            if (response.isSuccessful() && response.body() != null) {
              Log.d(TAG, "onResponse, successfull: " + new GsonBuilder().setPrettyPrinting().create().toJson(response));
              mView.showMessage("data successfully loaded");
            }
          }
        }

        @Override
        public void onFailure(Call<TaxiResponse> call, Throwable t) {
          if (isViewAttached()) {
            Log.d(TAG, "onFailure: " + t.getMessage());
            mView.showMessage("loading data error " + t.getMessage());
          }
        }
      });

    }
  }

  @Override
  public void bind(MainContract.View view) {
    this.mView = view;
  }

  @Override
  public void unbind() {
    mView = null;
  }

  @Override
  public boolean isViewAttached() {
    return mView != null;
  }
}
