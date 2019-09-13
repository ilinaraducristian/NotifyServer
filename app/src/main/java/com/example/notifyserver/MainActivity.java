package com.example.notifyserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

  public static final int BLUETOOTH_REQUEST_CODE = 1;
  private BluetoothAdapter adapter;
  private RecyclerView pairedDevicesList;
  private RecyclerViewAdapter pairedDevicesListAdapter;
  private Button refreshButton;
  private ArrayList<String> pairedDevices;
  private LinearLayoutManager recycleViewLayoutManager;
  private BluetoothServerSocket bluetoothServerSocket;
  private BluetoothServer server;

  public static final String BLUETOOTH_UUID = "a7b99f7b-47fe-4180-b25f-3cbf556b7d9b";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    adapter = BluetoothAdapter.getDefaultAdapter();
    refreshButton = findViewById(R.id.refreshButton);
    refreshButton.setEnabled(true);
    recycleViewLayoutManager = new LinearLayoutManager(this);

    refreshButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        server.start();
        Log.i("BLUETOOTH", "Listening for connection");
      }
    });
    pairedDevices = new ArrayList<>();
    pairedDevices.add("item1");
    pairedDevicesList = findViewById(R.id.pairedDevicesList);
    pairedDevicesListAdapter = new RecyclerViewAdapter(pairedDevices);
    pairedDevicesList.setAdapter(pairedDevicesListAdapter);
    pairedDevicesList.setLayoutManager(recycleViewLayoutManager);

    server = new BluetoothServer(adapter) {
      @Override
      public void onConnectionEstablished(BluetoothSocket socket) {
        Log.i("BLUETOOTH", "Device connected!");
      }
    };


    if(adapter == null) {
      if(adapter.isEnabled()) {
        startBluetoothServer();
      }else {
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetoothIntent, BLUETOOTH_REQUEST_CODE);
      }
    }else {
      Toast.makeText(this, "Device doesn't have bluetooth capabilities", Toast.LENGTH_SHORT).show();
    }
  }

  private void startBluetoothServer() {
    try {
      bluetoothServerSocket = adapter.listenUsingRfcommWithServiceRecord("NotifyServer", UUID.fromString(BLUETOOTH_UUID));
      bluetoothServerSocket.accept();
    }catch(IOException e) {
      e.printStackTrace();
      return;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    switch(requestCode) {
      case BLUETOOTH_REQUEST_CODE:
        if(resultCode == -1) {
          //user granted access
          startBluetoothServer();
        }else if(resultCode == 0) {
          //user denied access
          Toast.makeText(this, "Please enable bluetooth", Toast.LENGTH_SHORT).show();
        }
        break;
    }
  }

  private void listPairedDevices() {
    refreshButton.setEnabled(false);
    Set<BluetoothDevice> pairedBluetoothDevices = adapter.getBondedDevices();
    pairedDevices = new ArrayList<>();
    if (pairedBluetoothDevices.size() > 0) {
      for (BluetoothDevice device : pairedBluetoothDevices) {
        String deviceName = device.getName();
        pairedDevices.add(deviceName);
      }
      pairedDevicesList = findViewById(R.id.pairedDevicesList);
      pairedDevicesListAdapter = new RecyclerViewAdapter(pairedDevices);
      pairedDevicesList.setAdapter(pairedDevicesListAdapter);
      pairedDevicesList.setLayoutManager(recycleViewLayoutManager);
    }
    refreshButton.setEnabled(true);
  }


}
