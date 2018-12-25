package com.erkprog.madtaxi.data.api;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
  private static TaxiApi mApiService = null;
  private static final String BASE_URL = "http://openfreecabs.org/";

  public static TaxiApi getClient(Context context) {
    if (mApiService == null) {
      final OkHttpClient client = new OkHttpClient
          .Builder()
          .build();

      final Retrofit retrofit = new Retrofit.Builder()
          .addConverterFactory(GsonConverterFactory.create())
          .baseUrl(BASE_URL)
          .client(client)
          .build();

      mApiService = retrofit.create(TaxiApi.class);
    }
    return mApiService;
  }
}
