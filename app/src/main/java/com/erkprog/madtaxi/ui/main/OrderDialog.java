package com.erkprog.madtaxi.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.erkprog.madtaxi.R;


public class OrderDialog extends DialogFragment {

  private static final String TAG = "OrderDialog";

  public static final String SMS_NUM = "sms";
  public static final String PHONE_NUM = "phone";

  interface OnMakeOrderListener {
    void makeAnOrderUsingSms(String smsNum);

    void makeAnOrderUsingPhoneCall(String phoneNumber);
  }

  OnMakeOrderListener mListener;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    View v = LayoutInflater.from(getActivity()).inflate(R.layout.order_dialog, null);
    ImageView sms = v.findViewById(R.id.order_sms);
    ImageView phone = v.findViewById(R.id.order_phone);
    sms.setOnClickListener(v12 -> {
      mListener.makeAnOrderUsingSms(getArguments().getString(SMS_NUM));
      dismiss();
    });

    phone.setOnClickListener(v1 -> {
      mListener.makeAnOrderUsingPhoneCall(getArguments().getString(PHONE_NUM));
      dismiss();
    });

    return new AlertDialog.Builder(getActivity())
        .setView(v)
        .create();
  }

  public static OrderDialog newInstance(String sms, String phone) {
    OrderDialog dialog = new OrderDialog();
//    dialog.mListener = listener;
    Bundle args = new Bundle();
    args.putString(SMS_NUM, sms);
    args.putString(PHONE_NUM, phone);
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      mListener = (MainActivity) context;
    } catch (ClassCastException e) {
      Log.d(TAG, "onAttach: " + e.getMessage());
      dismiss();
    }
  }
}
