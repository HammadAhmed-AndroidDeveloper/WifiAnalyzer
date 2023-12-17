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

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.WifiViewHolder> {

    Context context;
    List<WifiInfo> modelData;

    public WifiAdapter(Context context, List<WifiInfo> modelData) {
        this.context = context;
        this.modelData = modelData;
    }

    @NonNull
    @Override
    public WifiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WifiViewHolder(LayoutInflater.from(context).inflate(R.layout.wifi_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WifiViewHolder holder, int position) {
        WifiInfo model = modelData.get(position);
        holder.wifiNameTV.setText(model.getWifiName() + "");
    }

    @Override
    public int getItemCount() {
        return modelData.size();
    }

    class WifiViewHolder extends RecyclerView.ViewHolder {

        TextView wifiNameTV;
        public WifiViewHolder(@NonNull View itemView) {
            super(itemView);
            wifiNameTV = itemView.findViewById(R.id.wifiNameTV);
        }
    }
}
