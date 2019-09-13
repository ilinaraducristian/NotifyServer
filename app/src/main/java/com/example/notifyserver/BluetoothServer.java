package com.example.notifyserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

public abstract class BluetoothServer extends Thread {

  private final BluetoothServerSocket bluetoothServerSocker;

  public BluetoothServer(BluetoothAdapter adapter) {
    BluetoothServerSocket tmp = null;
    try {
      tmp = adapter.listenUsingRfcommWithServiceRecord("NotifyServer", UUID.fromString(MainActivity.BLUETOOTH_UUID));
    }catch(IOException e) {
      e.printStackTrace();
    }
    bluetoothServerSocker = tmp;
  }

  public void run() {
    BluetoothSocket socket = null;

    while (true) {
      try {
        socket = bluetoothServerSocker.accept();
      } catch (IOException e) {
        e.printStackTrace();
        break;
      }

      if (socket != null) {
        onConnectionEstablished(socket);
        cancel();
        break;
      }
    }
  }

  public abstract void onConnectionEstablished(BluetoothSocket socket);

  public void cancel() {
    try {
      bluetoothServerSocker.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
