package com.reydw.notifyserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Process;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public abstract class BluetoothServer extends Thread {

  private static final String TAG = MainActivity.TAG;
  private final BluetoothServerSocket bluetoothServerSocker;
  private OutputStream os;

  public BluetoothServer() {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothServerSocket tmp = null;
    try {
      tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("NotifyServer", UUID.fromString(MainActivity.BLUETOOTH_UUID));
    }catch(IOException e) {
      Log.e(TAG, "Could not start server", e);
    }
    bluetoothServerSocker = tmp;
  }

  public void run() {
    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
    BluetoothSocket socket = null;
    Log.i(TAG, "Server started");
    while (true) {
      try {
        socket = bluetoothServerSocker.accept();
      } catch (IOException e) {break;}
      if (socket != null) {
        Log.i(TAG, "Connection established");
        while(true) {
          byte[] bytes = new byte[128];
          try {
            InputStream is = socket.getInputStream();
            is.read(bytes);
            onMessageReceived(bytes);
          } catch (IOException e) {break;}
        }
      }
    }
  }

  public abstract void onMessageReceived(byte[] bytes);

  public void close() {
    try {
      bluetoothServerSocker.close();
      Log.i(TAG, "Server closed");
    } catch (IOException e) {
      Log.e(TAG, "Socket close error", e);
    }
  }

  public boolean sendMessage(byte[] bytes) {
    if(os == null) return false;
    try {
      os.write(bytes);
    } catch (IOException e) {
      return false;
    }
    return true;
  }

}
