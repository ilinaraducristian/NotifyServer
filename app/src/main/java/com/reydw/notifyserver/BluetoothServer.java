package com.reydw.notifyserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Process;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public abstract class BluetoothServer extends Thread {

  private static final String TAG = MainActivity.TAG;
  private static final int MESSAGE_SIZE = 1024;

  private final BluetoothServerSocket bluetoothServerSocket;
  private OutputStream os;

  BluetoothServer() throws IOException{
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("NotifyServer", UUID.fromString(MainActivity.BLUETOOTH_UUID));
  }

  public void run() {
    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
    BluetoothSocket socket = null;
    Log.i(TAG, "Server started");
    while (true) {
      try {
        socket = bluetoothServerSocket.accept();
      } catch (IOException e) {
        break;
      }
      if (socket == null) continue;
      Log.i(TAG, "Connection established");
      onClientConnected(socket.getRemoteDevice().getName());
      while(true) {
        byte[] bytes = new byte[MESSAGE_SIZE];
        try {
          InputStream is = socket.getInputStream();
          is.read(bytes);
          onMessageReceived(bytes);
        } catch (IOException e) {
          onClientDisconnected();
          break;
        }
      }

    }
  }

  public abstract void onMessageReceived(byte[] bytes);
  public abstract void onClientConnected(String clientName);
  public abstract void onClientDisconnected();

  void close() {
    try {
      bluetoothServerSocket.close();
      Log.i(TAG, "Server closed");
    } catch (IOException e) {
      Log.e(TAG, "Socket close error", e);
    }
  }

  boolean sendMessage(byte[] bytes){
    if(os == null) return false;
    try {
      os.write(bytes);
    } catch (IOException e) {
      Log.e(TAG, "BluetoothServer sendMessage()", e);
      return false;
    }
    return true;
  }

  boolean sendMessage(Object object) {
    if(os == null) return false;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      ObjectOutput tmpos = new ObjectOutputStream(byteArrayOutputStream);
      tmpos.writeObject(object);
      tmpos.flush();
      byte[] b = byteArrayOutputStream.toByteArray();
      Log.i(TAG, "Message size: " + b.length);
      sendMessage(b);
    } catch (IOException e) {
      Log.e(TAG, "BluetoothServer sendMessage()", e);
      return false;
    }
    return true;
  }

}
