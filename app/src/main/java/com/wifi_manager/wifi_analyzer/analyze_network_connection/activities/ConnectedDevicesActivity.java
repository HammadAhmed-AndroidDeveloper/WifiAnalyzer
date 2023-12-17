package com.wifi_manager.wifi_analyzer.analyze_network_connection.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.R;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.adapters.ConnectedDevicesAdapter;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.adapters.DeviceInfo;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.connected_devices.DeviceFinder;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.connected_devices.interfaces.OnDeviceFoundListener;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.connected_devices.models.DeviceItem;

import java.util.ArrayList;
import java.util.List;

public class ConnectedDevicesActivity extends AppCompatActivity {

    private RecyclerView devicesInfoRV;
    private final List<DeviceInfo> deviceInfo = new ArrayList<>();
    private long start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_devices);
        setTitle("Connected Devices");
        devicesInfoRV = findViewById(R.id.devicesInfoRV);
        devicesInfoRV.setLayoutManager(new LinearLayoutManager(this));
        devicesInfoRV.setHasFixedSize(true);

        DeviceFinder devicesFinder = new DeviceFinder(this, new OnDeviceFoundListener() {

            @Override
            public void onStart(DeviceFinder deviceFinder) {
                start = System.currentTimeMillis();
            }

            @Override
            public void onFinished(DeviceFinder deviceFinder, List<DeviceItem> deviceItems) {
                end = System.currentTimeMillis();
                deviceInfo.clear();
                float time = (end - start)/1000f;
                for (DeviceItem deviceItem : deviceItems) {
                    deviceInfo.add(new DeviceInfo(deviceItem.getDeviceName(), deviceItem.getIpAddress(), deviceItem.getMacAddress()));
                }

                devicesInfoRV.setAdapter(new ConnectedDevicesAdapter(ConnectedDevicesActivity.this, deviceInfo));
            }

            @Override
            public void onFailed(DeviceFinder deviceFinder, int errorCode) {

            }
        });

        devicesFinder.setTimeout(500).start();
    }
}