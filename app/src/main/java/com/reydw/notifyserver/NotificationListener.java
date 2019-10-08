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
    String packageName = sbn.getPackageName();
    Notification notification = sbn.getNotification();
    String notificationTitle = notification.extras.getString("android.title");
    String notificationText = notification.extras.getString("android.text");
    String notificationSubtext = notification.extras.getString("android.subtext");

    Intent notificationIntent = new Intent("com.reydw.notifyserver.NOTIFICATION_RECEIVED");
    switch (packageName) {
      //noinspection SpellCheckingInspection
      case "com.samsung.android.messaging":
        notificationIntent.putExtra("appname", "Messaging");
        notificationIntent.putExtra("title", notificationTitle);
        notificationIntent.putExtra("text", notificationText);
        notificationIntent.putExtra("subtext", notificationSubtext);
        sendBroadcast(notificationIntent);
        break;
      //noinspection SpellCheckingInspection
      case "com.whatsapp":
        //noinspection SpellCheckingInspection
        notificationIntent.putExtra("appname", "WhatsApp");
        notificationIntent.putExtra("title", notificationTitle);
        notificationIntent.putExtra("text", notificationText);
        notificationIntent.putExtra("subtext", notificationSubtext);
        sendBroadcast(notificationIntent);
        break;
    }
  }

}
