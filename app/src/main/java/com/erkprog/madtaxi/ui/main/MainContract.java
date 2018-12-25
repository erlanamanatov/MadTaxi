package com.erkprog.madtaxi.ui.main;

public interface MainContract {

  interface View {

    void showMessage(String message);

  }

  interface Presenter {

    void bind(View v);

    void unbind();

    boolean isViewAttached();

    void loadData();

  }
}
