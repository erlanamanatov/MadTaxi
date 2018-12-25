package com.erkprog.madtaxi.util;

import android.util.Log;

public class MyUtil {

  public static void logd(String tag, String message) {
    Log.d(tag, String.format("%s [%s]", message, Thread.currentThread().getName()));
  }
}
