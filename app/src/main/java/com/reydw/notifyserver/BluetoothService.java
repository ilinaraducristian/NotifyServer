package com.reydw.notifyserver;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.reydw.notifyserver.actions.NotificationAction;

import java.io.IOException;


public class BluetoothService extends Service {

  private static final String TAG = MainActivity.TAG;

  private NotificationManagerCompat notificationManager;
  private NotificationCompat.Builder notificationBuilder;
  private BluetoothServer bluetoothServer;
  private NotificationBroadcastReceiver notificationBroadcastReceiver;

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

    notificationBroadcastReceiver = new NotificationBroadcastReceiver();

    try {
      bluetoothServer = new BluetoothServer(){

        @Override
        public void onMessageReceived(byte[] bytes) {
  //        Log.i(TAG, "Message Received: " + new String(bytes));
        }

        @Override
        public void onClientConnected(String clientName) {
          updateNotification(clientName + " connected");
        }

        @Override
        public void onClientDisconnected() {
          updateNotification("Client disconnected");
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
      registerReceiver(notificationBroadcastReceiver, new IntentFilter("com.reydw.notifyserver.FOO"));
      startForeground(1, notificationBuilder.build());
    }
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    // stop bluetooth server
    bluetoothServer.close();
    unregisterReceiver(notificationBroadcastReceiver);
    stopForeground(true);
    stopSelf();
  }

  private void updateNotification(String text) {
    if(notificationBuilder == null || notificationManager == null) return;
    notificationBuilder.setContentText(text);
    notificationManager.notify(1, notificationBuilder.build());
  }

  class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    @SuppressWarnings({"all"})
    public void onReceive(Context context, Intent intent) {
      Bundle notification = intent.getExtras();
      NotificationAction notificationAction = new NotificationAction(notification);
      Log.i(TAG, notificationAction.toString());
      bluetoothServer.sendAction(notificationAction);
    }
  }

}
