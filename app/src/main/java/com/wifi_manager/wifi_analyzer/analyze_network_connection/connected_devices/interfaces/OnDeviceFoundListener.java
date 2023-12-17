package com.wifi_manager.wifi_analyzer.analyze_network_connection.connected_devices.interfaces;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.connected_devices.DeviceFinder;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.connected_devices.models.DeviceItem;

import java.util.List;


public interface OnDeviceFoundListener {
    void onStart(DeviceFinder deviceFinder);
    void onFinished(DeviceFinder deviceFinder, List<DeviceItem> deviceItems);
    void onFailed(DeviceFinder deviceFinder, int errorCode);
}
