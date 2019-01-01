package com.erkprog.madtaxi.ui.main;

import android.location.Location;
import android.support.annotation.NonNull;

import com.erkprog.madtaxi.R;
import com.erkprog.madtaxi.data.LocationHelper;
import com.erkprog.madtaxi.data.api.TaxiApi;
import com.erkprog.madtaxi.data.entity.Company;
import com.erkprog.madtaxi.data.entity.Contact;
import com.erkprog.madtaxi.data.entity.Driver;
import com.erkprog.madtaxi.data.entity.TaxiResponse;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  MainContract.View view;

  @Mock
  TaxiApi taxiApi;

  @Mock
  LocationHelper locationHelper;
  MainPresenter presenter;

  private static String TAG = "Testing";

  @Before
  public void setUp() {
    presenter = new MainPresenter(taxiApi, locationHelper);
    presenter.bind(view);
  }

  @BeforeClass
  public static void setUpRxSchedulers() {
    Scheduler immediate = new Scheduler() {
      @Override
      public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
        // this prevents StackOverflowErrors when scheduling with a delay
        return super.scheduleDirect(run, 0, unit);
      }

      @Override
      public Scheduler.Worker createWorker() {
        return new ExecutorScheduler.ExecutorWorker(Runnable::run);
      }
    };

    RxJavaPlugins.setInitIoSchedulerHandler(scheduler -> immediate);
    RxJavaPlugins.setInitComputationSchedulerHandler(scheduler -> immediate);
    RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
    RxJavaPlugins.setInitSingleSchedulerHandler(scheduler -> immediate);
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);
  }

  @Test
  public void loadData_WhenOnSuccessResponseAndViewIsAttached_ShouldDisplayTaxiItems() throws IOException {
    String resultAddress = "some address";
    when(locationHelper.getAddress(anyDouble(), anyDouble())).thenReturn(resultAddress);
    when(taxiApi.getNearistTaxi(anyDouble(), anyDouble())).thenReturn(Observable.just(getFakeTaxiResponse()));
    presenter.loadData(123.12, 123.42);
    verify(view).displayNearistTaxiCabs(any());
  }

  @Test
  public void loadData_WhenOnSuccessResponseAndViewIsNotAttached_ShouldNotDisplayAnything() throws IOException {
    String resultAddress = "some address";
    when(locationHelper.getAddress(anyDouble(), anyDouble())).thenReturn(resultAddress);
    when(taxiApi.getNearistTaxi(anyDouble(), anyDouble())).thenReturn(Observable.just(getFakeTaxiResponse()));
    presenter.unbind();
    presenter.loadData(123.12, 123.42);
    verify(view, never()).displayNearistTaxiCabs(any());
  }

  @Test
  public void loadData_WhenCompaniesInResponseNull_ShouldShowErrorMessage() throws IOException {
    String resultAddress = "some address";
    when(locationHelper.getAddress(anyDouble(), anyDouble())).thenReturn(resultAddress);
    when(taxiApi.getNearistTaxi(anyDouble(), anyDouble())).thenReturn(Observable.just(new TaxiResponse()));
    presenter.loadData(123.12, 123.42);
    verify(view).showMessage(R.string.error_loading_data);
  }

  @Test
  public void loadData_WhenOnErrorAndViewIsNotAttached_ShouldNotShowAnyMessage() throws IOException {
    String resultAddress = "some address";
    when(locationHelper.getAddress(anyDouble(), anyDouble())).thenReturn(resultAddress);
    when(taxiApi.getNearistTaxi(anyDouble(), anyDouble())).thenReturn(Observable.just(new TaxiResponse()));
    presenter.unbind();
    presenter.loadData(123.12, 123.42);
    verify(view, never()).showMessage(anyInt());
  }

  @Test
  public void getAddress_WhenOnSuccessAndViewIsAttached_ShouldShowAddress() throws IOException {
    String address = "123 address";
    when(locationHelper.getAddress(anyDouble(), anyDouble())).thenReturn(address);
    presenter.getAddress(123.123, 123.123);
    verify(view).showAddress(address);
  }

  @Test
  public void getAddress_WhenOnSuccessAndViewIsNotAttached_ShouldNowShowAny() throws IOException {
    String address = "123 address";
    when(locationHelper.getAddress(anyDouble(), anyDouble())).thenReturn(address);
    presenter.unbind();
    presenter.getAddress(123.123, 123.123);
    verify(view, never()).showAddress(address);
  }

  @Test
  public void getCurrentLocation_WhenOnLocationChangedAndViewIsAttached_ShouldCenterMapToLocation() {
    ArgumentCaptor<LocationHelper.OnLocationChangedListener> locationChangedListenerArgumentCaptor = ArgumentCaptor.forClass(LocationHelper
        .OnLocationChangedListener.class);
    MainPresenter spy = spy(new MainPresenter(taxiApi, locationHelper));
    spy.bind(view);
    doNothing().when(spy).loadData(anyDouble(), anyDouble());

    spy.getCurrentLocation();
    Location location = mock(Location.class);
    verify(view).onGettingLocation();
    verify(locationHelper, times(1)).getLocation(locationChangedListenerArgumentCaptor.capture());
    locationChangedListenerArgumentCaptor.getValue().onLocationChanged(location);
    verify(view).onLocationFound();
    verify(view).centerMapToLocation(location);
  }

  @Test
  public void getCurrentLocation_WhenOnLocationChangedAndViewIsNotAttached() {
    ArgumentCaptor<LocationHelper.OnLocationChangedListener> locationChangedListenerArgumentCaptor = ArgumentCaptor.forClass(LocationHelper
        .OnLocationChangedListener.class);
    MainPresenter spy = spy(new MainPresenter(taxiApi, locationHelper));
    Location location = mock(Location.class);
    spy.bind(view);
    spy.getCurrentLocation();
    spy.unbind();
    verify(view).onGettingLocation();
    verify(locationHelper, times(1)).getLocation(locationChangedListenerArgumentCaptor.capture());
    locationChangedListenerArgumentCaptor.getValue().onLocationChanged(location);
    verify(view, never()).onLocationFound();
    verify(view, never()).centerMapToLocation(location);
  }

  private TaxiResponse getFakeTaxiResponse() {
    TaxiResponse response = new TaxiResponse();
    Company company = getFakeCompany();
    List<Company> companyList = Arrays.asList(company);
    response.setCompanies(companyList);
    return response;
  }

  private Company getFakeCompany() {
    Company company = new Company();
    company.setName("namba");
    company.setContacts(Arrays.asList(getFakeContact()));
    company.setDrivers(Arrays.asList(getFakeDriver(), getFakeDriver(), getFakeDriver()));
    return company;
  }

  private Driver getFakeDriver() {
    Driver driver = new Driver();
    driver.setLat(new Random().nextDouble());
    driver.setLon(new Random().nextDouble());
    return driver;
  }

  private Contact getFakeContact() {
    Contact contact = new Contact();
    contact.setType("sms");
    contact.setContact("123");
    return contact;
  }
}