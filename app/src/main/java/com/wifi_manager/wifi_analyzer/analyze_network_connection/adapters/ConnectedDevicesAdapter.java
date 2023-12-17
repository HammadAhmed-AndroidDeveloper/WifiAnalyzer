package com.wifi_manager.wifi_analyzer.analyze_network_connection.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.R;

import java.util.List;

public class ConnectedDevicesAdapter extends RecyclerView.Adapter<ConnectedDevicesAdapter.ConnectedDevicesViewHolder> {

    Context context;
    List<DeviceInfo> modelData;

    public ConnectedDevicesAdapter(Context context, List<DeviceInfo> modelData) {
        this.context = context;
        this.modelData = modelData;
    }

    @NonNull
    @Override
    public ConnectedDevicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConnectedDevicesViewHolder(LayoutInflater.from(context).inflate(R.layout.devices_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ConnectedDevicesViewHolder holder, int position) {
        DeviceInfo info = modelData.get(position);
        holder.deviceName.setText("Device Name: " + info.getName());
        holder.ipAddress.setText("IP Address: " + info.getIp());
        holder.macAddress.setText("MAC Address: " + info.getMac());
    }

    @Override
    public int getItemCount() {
        return modelData.size();
    }


    class ConnectedDevicesViewHolder extends RecyclerView.ViewHolder {

        TextView deviceName, ipAddress, macAddress;
         public ConnectedDevicesViewHolder(@NonNull View itemView) {
             super(itemView);
             deviceName = itemView.findViewById(R.id.deviceNameTV);
             ipAddress = itemView.findViewById(R.id.ipAddressTV);
             macAddress = itemView.findViewById(R.id.macAddressTV);
         }
     }
}
