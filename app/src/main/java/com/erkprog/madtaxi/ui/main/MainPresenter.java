package com.erkprog.madtaxi.ui.main;

import com.erkprog.madtaxi.data.api.TaxiApi;
import com.erkprog.madtaxi.data.entity.Company;
import com.erkprog.madtaxi.data.entity.TaxiResponse;
import com.erkprog.madtaxi.util.MyUtil;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
      mApiService.getNearistTaxi(42.882, 74.584)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Observer<TaxiResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(TaxiResponse taxiResponse) {
              if (isViewAttached()) {
                mView.showMessage("Data loaded");
                MyUtil.logd(TAG, "successfull response: " + taxiResponse.getSuccess());
              }
            }

            @Override
            public void onError(Throwable e) {
              mView.showMessage("Error loading data " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
          });


//      Observable<TaxiResponse> response = mApiService.getNearistTaxi(42.882, 74.584);

//      Observable<Company> com = response.flatMap(txres -> {
//        return Observable.fromIterable(txres.getCompanies());
//      });


//      com.doOnNext(company -> Log.d(TAG, "doOnNext: " + company.getName()))
//          .subscribeOn(Schedulers.io())
//          .observeOn(AndroidSchedulers.mainThread())
//          .subscribe(new Observer<Company>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Company company) {
//              Log.d(TAG, "onNext: " + company.getName());
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//          });

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
