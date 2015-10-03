package com.mandaptak.android.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.mandaptak.android.Login.LoginActivity;

public class IncomingSms extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    final Bundle bundle = intent.getExtras();
    try {
      if (bundle != null) {
        final Object[] pdusObj = (Object[]) bundle.get("pdus");
        for (int i = 0; i < pdusObj.length; i++) {
          SmsMessage currentMessage =
              SmsMessage.createFromPdu((byte[]) pdusObj[i]);
          String phoneNumber = currentMessage.getDisplayOriginatingAddress();
          String senderNum = phoneNumber;
          String message = currentMessage.getDisplayMessageBody();
          try {
            if (senderNum.endsWith("MNDPTK")) {
              int length = message.length();
              try {
                LoginActivity.setCode(message.substring(length - 4, length));
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}