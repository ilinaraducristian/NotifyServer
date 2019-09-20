package com.reydw.notifyserver;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;


public class BluetoothService extends Service {

  private static final String TAG = MainActivity.TAG;

  private NotificationCompat.Builder notificationBuilder;
  private BluetoothServer bluetoothServer;

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
    notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "0")
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle("NotifyServer")
      .setContentText("Service started")
      .setContentIntent(mainActivityPendingIntent)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    bluetoothServer = new BluetoothServer(){
      @Override
      public void onMessageReceived(byte[] bytes) {
        Log.i(TAG, "Message Received: " + new String(bytes));
      }
    };
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    bluetoothServer.start();
    startForeground(1, notificationBuilder.build());
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    // stop bluetooth server
    bluetoothServer.close();
    stopForeground(true);
    stopSelf();
  }

  class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.i(TAG, "onReceive: " + intent.getAction());
    }
  }

}
