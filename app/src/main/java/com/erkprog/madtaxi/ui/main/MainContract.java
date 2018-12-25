package com.erkprog.madtaxi.ui.main;

public interface MainContract {

  interface View {

  }

  interface Presenter {

    void bind(View v);

    void unbind();

    boolean isViewAttached();

    void loadData();

  }
}
