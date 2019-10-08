package com.reydw.notifyserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Parcel;
import android.os.Process;
import android.util.Log;

import com.reydw.notifyserver.actions.NotificationAction;

import java.io.IOException;
import java.io.InputStream;
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
    InputStream is;
    Log.i(TAG, "Server started");
    while (true) {
      try {
        socket = bluetoothServerSocket.accept();
      } catch (IOException e) {
        break;
      }
      if (socket == null) continue;
      try{
        is = socket.getInputStream();
        os = socket.getOutputStream();
      }catch(IOException e) {
        continue;
      }
      Log.i(TAG, "Connection established");
      onClientConnected(socket.getRemoteDevice().getName());
      while(true) {
        byte[] bytes = new byte[MESSAGE_SIZE];
        try {
          is.read(bytes);
          onMessageReceived(bytes);
        } catch (IOException e) {
          onClientDisconnected();
          os = null;
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

  boolean sendAction(byte[] bytes){
    if(os == null) return false;
    try {
//      for (byte b : bytes) {
//        String st = String.format("%02X", b);
//        Log.i(TAG + "plm", st);
//      }
//      Log.i(TAG + "plm", "size " + bytes.length);
      os.write(bytes);
    } catch (IOException e) {
      Log.e(TAG, "BluetoothServer sendAction()", e);
      return false;
    }
    return true;
  }

  boolean sendAction(NotificationAction action) {
    if(os == null) return false;
//    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//    try {
//      ObjectOutput tmpos = new ObjectOutputStream(byteArrayOutputStream);
//      tmpos.writeObject(action);
//      tmpos.flush();
//      byte[] bytes = byteArrayOutputStream.toByteArray();
//      sendAction(bytes);
//    } catch (IOException e) {
//      Log.e(TAG, "BluetoothServer sendAction()", e);
//      return false;
//    }
    Parcel parcel = Parcel.obtain();
    action.writeToParcel(parcel, 0);
    byte[] bytes = parcel.marshall();
    parcel.recycle();
    return sendAction(bytes);
  }

}
