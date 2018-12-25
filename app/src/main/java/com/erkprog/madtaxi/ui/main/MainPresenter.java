package com.erkprog.madtaxi.ui.main;

public class MainPresenter implements MainContract.Presenter {

  private static final String TAG = "MainPresenter";

  MainContract.View mView;

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
