package com.erkprog.madtaxi.data.api;

import com.erkprog.madtaxi.data.entity.TaxiResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TaxiApi {

  @GET("nearest/{lat}/{lng}")
  Observable<TaxiResponse> getNearistTaxi(@Path("lat") Double lat,
                                          @Path("lng") Double lng);
}
