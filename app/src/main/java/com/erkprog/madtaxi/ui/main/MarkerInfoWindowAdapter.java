package com.erkprog.madtaxi.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.erkprog.madtaxi.R;
import com.erkprog.madtaxi.data.entity.Company;
import com.erkprog.madtaxi.data.entity.TaxiCab;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

  private View mWindow;
  private Context mContext;

  MarkerInfoWindowAdapter(Context context) {
    mContext = context;
    mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_marker_window, null);
  }

  private void render(Marker marker, View view) {
    ImageView imgLogo = view.findViewById(R.id.company_logo);
    TextView tvCompanyName = view.findViewById(R.id.company_name);
    TextView sms = view.findViewById(R.id.sms);
    TextView phone = view.findViewById(R.id.phone);
    TaxiCab taxiCab = (TaxiCab) marker.getTag();
    if (taxiCab != null) {
      String companyName = taxiCab.getCompanyName();
      tvCompanyName.setText(companyName);
      sms.setText(String.format("sms: %s", taxiCab.getSmsNum()));
      phone.setText(String.format("tel: %s", taxiCab.getPhoneNum()));
      if (companyName != null) {
        switch (companyName) {
          case Company.COMPANY_NAMBA:
            imgLogo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.logo_namba));
            break;
          case Company.COMPANY_SMS:
            imgLogo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.logo_sms));
            break;
          default:
            imgLogo.setVisibility(View.GONE);
        }
      } else {
        imgLogo.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public View getInfoWindow(Marker marker) {
    render(marker, mWindow);
    return mWindow;
  }

  @Override
  public View getInfoContents(Marker marker) {
    render(marker, mWindow);
    return mWindow;
  }
}
