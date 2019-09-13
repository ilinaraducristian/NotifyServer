package com.example.notifyserver;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

  private ArrayList<String> pairedDevicesNames = new ArrayList<>();

  public RecyclerViewAdapter(ArrayList<String> pairedDevicesNames) {
    this.pairedDevicesNames = pairedDevicesNames;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.paired_device, viewGroup, false);
    ViewHolder viewHolder = new ViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
    viewHolder.pairedDeviceButton.setText(pairedDevicesNames.get(i));
    viewHolder.pairedDeviceButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.i("BLUETOOTH", String.format("item %d clicked", i));
      }
    });
  }

  @Override
  public int getItemCount() {
    return pairedDevicesNames.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    Button pairedDeviceButton;
    RelativeLayout pairedDeviceLayout;

    public ViewHolder(View pairedDeviceView) {
      super(pairedDeviceView);
      pairedDeviceButton = pairedDeviceView.findViewById(R.id.pairedDeviceButton);
      pairedDeviceLayout = pairedDeviceView.findViewById(R.id.pairedDeviceLayout);
    }

  }

}
