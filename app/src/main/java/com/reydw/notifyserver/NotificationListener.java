package com.reydw.notifyserver;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationListener extends NotificationListenerService {

  private static final String TAG = MainActivity.TAG;

  @Override
  public void onListenerConnected() {
    Log.i(TAG, "onListenerConnected");
  }

  @Override
  public void onListenerDisconnected() {
    Log.i(TAG, "onListenerDisconnected: ");
  }

  @Override
  public void onNotificationPosted(StatusBarNotification sbn) {
    Log.i(TAG, "onNotificationPosted " + sbn.getPackageName());
  }

  @Override
  public void onNotificationRemoved(StatusBarNotification sbn) {
    Log.i(TAG, String.format("%s", sbn.getPackageName()));
  }
}
