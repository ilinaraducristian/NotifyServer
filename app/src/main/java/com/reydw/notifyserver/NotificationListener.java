package com.reydw.notifyserver;

import android.app.Notification;
import android.content.Intent;
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
    Log.i(TAG, "onNotificationPosted: beforecheck");
    String packageName = sbn.getPackageName();
    Notification notification = sbn.getNotification();
    String notificationTitle = notification.extras.getString("android.title");
    String notificationText = notification.extras.getString("android.text");
    String notificationSubtext = notification.extras.getString("android.subtext");

    switch (packageName) {
      //noinspection SpellCheckingInspection
      case "com.samsung.android.messaging":
      //noinspection SpellCheckingInspection
      case "com.whatsapp":
        Intent notificationIntent = new Intent(this, BluetoothService.NotificationBroadcastReceiver.class);
        //noinspection SpellCheckingInspection
        notificationIntent.putExtra("appname", "WhatsApp");
        notificationIntent.putExtra("title", notificationTitle);
        notificationIntent.putExtra("text", notificationText);
        notificationIntent.putExtra("subtext", notificationSubtext);
        sendBroadcast(notificationIntent);
        Log.i(TAG, "onNotificationPosted: ");
        break;
    }
  }

  @Override
  public void onNotificationRemoved(StatusBarNotification sbn) {
//    Log.i(TAG, String.format("%s", sbn.getPackageName()));
  }
}
