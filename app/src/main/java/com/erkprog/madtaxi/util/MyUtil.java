package com.erkprog.madtaxi.util;

import android.util.Log;

import com.erkprog.madtaxi.R;
import com.erkprog.madtaxi.data.entity.Company;

public class MyUtil {

  public static void logd(String tag, String message) {
    Log.d(tag, String.format("%s [%s]", message, Thread.currentThread().getName()));
  }

  public static int getLogo(String companyName) {
    int logoRes = R.drawable.car;
    switch (companyName) {
      case Company.COMPANY_NAMBA:
        logoRes = R.drawable.car_namba;
        break;
      case Company.COMPANY_SMS:
        logoRes = R.drawable.car_sms;
        break;
    }
    return logoRes;
  }
}
