package com.reydw.notifyserver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

  public static final int BLUETOOTH_REQUEST_CODE = 1;
  public static final String TAG = "NotifyServiceDebuggingTAG";
  private Intent bluetoothServiceIntent;
  private BluetoothAdapter adapter;
  private Button startServerButton;
  private Button stopServerButton;
  private BluetoothStateChangeBroadcastReceiver bluetoothStateChangeBroadcastReceiver;

  public static final String BLUETOOTH_UUID = "a7b99f7b-47fe-4180-b25f-3cbf556b7d9b";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    adapter = BluetoothAdapter.getDefaultAdapter();
    startServerButton = findViewById(R.id.startServerButton);
    stopServerButton = findViewById(R.id.stopServerButton);
    bluetoothServiceIntent = new Intent(this, BluetoothService.class);
    disableUIButtons();

    bluetoothStateChangeBroadcastReceiver = new BluetoothStateChangeBroadcastReceiver();
    registerReceiver(bluetoothStateChangeBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

    if(adapter != null) {
      if(adapter.isEnabled()) {
        enableUIButtons();
      }else {
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetoothIntent, BLUETOOTH_REQUEST_CODE);
      }
    }else {
      Toast.makeText(this, "Device doesn't have bluetooth capabilities", Toast.LENGTH_SHORT).show();
    }

  }

  public void startBluetoothService(View view) {
    startService(bluetoothServiceIntent);
  }

  public void stopBluetoothService(View view) {
    stopService(bluetoothServiceIntent);
  }

  void enableUIButtons() {
    startServerButton.setEnabled(true);
    stopServerButton.setEnabled(true);
  }

  void disableUIButtons() {
    startServerButton.setEnabled(false);
    stopServerButton.setEnabled(false);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    switch(requestCode) {
      case BLUETOOTH_REQUEST_CODE:
        if(resultCode == -1) {
          //user granted access
          enableUIButtons();
        }else if(resultCode == 0) {
          //user denied access
          Toast.makeText(this, "Please enable bluetooth", Toast.LENGTH_SHORT).show();
        }
        break;
    }
  }

  @Override
  protected void onDestroy() {
    unregisterReceiver(bluetoothStateChangeBroadcastReceiver);
    super.onDestroy();
  }

  class BluetoothStateChangeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
      switch (bluetoothState) {
        case BluetoothAdapter.STATE_OFF:
          disableUIButtons();
          break;
        case BluetoothAdapter.STATE_ON:
          enableUIButtons();
          break;
      }
    }
  }

}
