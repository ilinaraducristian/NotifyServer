package com.reydw.notifyserver;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;


public class BluetoothService extends Service {

  private static final String TAG = MainActivity.TAG;

  private NotificationManagerCompat notificationManager;
  private NotificationCompat.Builder notificationBuilder;
  private BluetoothServer bluetoothServer;
  private boolean hasActiveConnection = false;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    Intent mainActivityIntent = new Intent(this, MainActivity.class);
    mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);
    notificationManager = NotificationManagerCompat.from(this);

    notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "0")
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle("NotifyServer")
      .setContentText("Service running")
      .setContentIntent(mainActivityPendingIntent)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    try {
      bluetoothServer = new BluetoothServer(){

        @Override
        public void onMessageReceived(byte[] bytes) {
  //        Log.i(TAG, "Message Received: " + new String(bytes));
        }

        @Override
        public void onClientConnected(String clientName) {
          updateNotification(clientName + " connected");
          hasActiveConnection = true;
        }

        @Override
        public void onClientDisconnected() {
          updateNotification("Client disconnected");
          hasActiveConnection = false;
        }
      };
    }catch(IOException e) {
      Log.e(TAG, "Could not start server", e);

    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if(bluetoothServer == null) {
      Toast.makeText(this, "Could not start server", Toast.LENGTH_SHORT).show();
    }else {
      bluetoothServer.start();
      startForeground(1, notificationBuilder.build());
    }
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    // stop bluetooth server
    bluetoothServer.close();
    stopForeground(true);
    stopSelf();
  }

  private void updateNotification(String text) {
    if(notificationBuilder == null || notificationManager == null) return;
    notificationBuilder.setContentText(text);
    notificationManager.notify(1, notificationBuilder.build());
  }

  static class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    @SuppressWarnings({"all"})
    public void onReceive(Context context, Intent intent) {
      Log.i(TAG, "onReceive: ");
      Bundle notification = intent.getExtras();
      //noinspection SpellCheckingInspection
      String appname = notification.get("appname").toString();
      String title = notification.get("title").toString();
      String text = notification.get("text").toString();
      String subtext = notification.get("subtext").toString();
      NotificationForClient notificationForClient = new NotificationForClient(appname, title, text, subtext);
      Log.i(TAG, notificationForClient.toString());
      if(!hasActiveConnection) return;
      bluetoothServer.sendMessage(notificationForClient);
    }
  }

  @SuppressWarnings({"unused", "SpellCheckingInspection", "FieldCanBeLocal"})
  class NotificationForClient implements Serializable{

    private static final long serialVersionUID = 69;

    private final String appname;
    private final String title;
    private final String text;
    private final String subtext;

    NotificationForClient(String appname, String title, String text, String subtext) {
      this.appname = appname;
      this.title = title;
      this.text = text;
      this.subtext = subtext;
    }

    @Override
    public String toString() {
      return "NotificationForClient{" +
        "appname='" + appname + '\'' +
        ", title='" + title + '\'' +
        ", text='" + text + '\'' +
        ", subtext='" + subtext + '\'' +
        '}';
    }
  }

}
