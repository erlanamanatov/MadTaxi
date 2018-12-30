package com.erkprog.madtaxi.ui.main;

import com.erkprog.madtaxi.data.LocationHelper;
import com.erkprog.madtaxi.data.api.TaxiApi;
import com.erkprog.madtaxi.data.entity.Company;
import com.erkprog.madtaxi.data.entity.Contact;
import com.erkprog.madtaxi.data.entity.Driver;
import com.erkprog.madtaxi.data.entity.TaxiCab;
import com.erkprog.madtaxi.data.entity.TaxiClusterItem;
import com.erkprog.madtaxi.util.MyUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {

  private static final String TAG = "MainPresenter";

  private MainContract.View mView;
  private TaxiApi mApiService;
  private LocationHelper mLocationHelper;
  private CompositeDisposable mDisposable = new CompositeDisposable();

  MainPresenter(TaxiApi apiService, LocationHelper locationHelper) {
    mApiService = apiService;
    mLocationHelper = locationHelper;
  }

  @Override
  public void loadData(double lat, double lng) {
    mDisposable.clear();

    getAddress(lat, lng);

    if (mApiService != null) {
      /**
       * Fetching nearist taxi drivers from api
       * flatMap is used to get observable companies from taxiResponse
       * after that flatMap is used to get observable taxiCabs from companies
       * @see #getTaxiCabs(Company)
       * map operator is used to transform TaxiCab objects into TaxiClusterItem objects
       * toList operator is used to return all taxiClusterItems in one list
       */
      Single<List<TaxiClusterItem>> taxiObs = mApiService.getNearistTaxi(lat, lng)
          .flatMap(taxiResponse -> Observable.fromIterable(taxiResponse.getCompanies()))
          .flatMap(company -> Observable.fromIterable(getTaxiCabs(company)))
          .map(TaxiClusterItem::new)
          .subscribeOn(Schedulers.io())
          .doOnNext(taxiClusterItem -> MyUtil.logd(TAG, "taxiClusterItem onNext(): " + taxiClusterItem.getTaxiCab().toString()))
          .toList()
          .observeOn(AndroidSchedulers.mainThread());

      mDisposable.add(taxiObs.subscribeWith(new DisposableSingleObserver<List<TaxiClusterItem>>() {
        @Override
        public void onSuccess(List<TaxiClusterItem> taxiItems) {
          if (isViewAttached()) {
            MyUtil.logd(TAG, "Data loaded successfully, taxi count = " + taxiItems.size());
            mView.displayNearistTaxiCabs(taxiItems);
          }
        }

        @Override
        public void onError(Throwable e) {
          if (isViewAttached()) {
            mView.showMessage("Can not load nearist taxicabs." + e.getMessage());
          }
          MyUtil.logd(TAG, "Loading taxi cabs error " + e.getMessage());
        }
      }));
    }
  }

  @Override
  public void onInfoWindowClicked(TaxiCab taxiCab) {
    mView.showOrderDialog(taxiCab);
  }

  private List<TaxiCab> getTaxiCabs(Company company) {
    List<TaxiCab> list = new ArrayList<>();

    String tel = "";
    String smsNum = "";
    String companyName = company.getName();
    for (Contact contact : company.getContacts()) {
      if (contact.getType().equals(Contact.TYPE_SMS)) {
        smsNum = contact.getContact();
      }
      if (contact.getType().equals(Contact.TYPE_PHONE)) {
        tel = contact.getContact();
      }
    }

    for (Driver driver : company.getDrivers()) {
      list.add(new TaxiCab(companyName, smsNum, tel, driver.getLat(), driver.getLon()));
    }
    return list;
  }

  @Override
  public void getAddress(double lat, double lng) {
    mDisposable.add(Single.fromCallable(() -> mLocationHelper.getAddress(lat, lng)).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<String>() {
          @Override
          public void onSuccess(String s) {
            if (isViewAttached()) {
              mView.showAddress(s);
            }
          }

          @Override
          public void onError(Throwable e) {
            mView.showAddress("");
          }
        }));
  }

  @Override
  public void bind(MainContract.View view) {
    this.mView = view;
  }

  @Override
  public void unbind() {
    mDisposable.dispose();
    mView = null;
  }

  @Override
  public boolean isViewAttached() {
    return mView != null;
  }

  @Override
  public void getCurrentLocation() {
    mView.onGettingLocation();
    mLocationHelper.getLocation(location -> {
      if (isViewAttached()) {
        mView.setIconsDefaultState();
        mView.centerMapToLocation(location);
        loadData(location.getLatitude(), location.getLongitude());
      }
    });
  }
}
