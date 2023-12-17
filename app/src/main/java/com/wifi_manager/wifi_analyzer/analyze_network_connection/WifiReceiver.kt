package com.wifi_manager.wifi_analyzer.analyze_network_connection

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.wifi_manager.wifi_analyzer.analyze_network_connection.adapters.WifiAdapter
import com.wifi_manager.wifi_analyzer.analyze_network_connection.adapters.WifiInfo
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class WifiReceiver(var wifiManager: WifiManager, var wifiDeviceList: RecyclerView) :
    BroadcastReceiver() {
    var sb: StringBuilder? = null
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION == action) {
            sb = StringBuilder()
            Dexter.withContext(context).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            val wifiList = wifiManager.scanResults
                            val deviceList: MutableList<WifiInfo> = ArrayList()
                            for (scanResult in wifiList) {
                                sb!!.append("").append(scanResult.SSID)
                                //                            .append(" - ").append(scanResult.capabilities);
//                            deviceList.add(scanResult.SSID + " - " + scanResult.capabilities);
//                            deviceList.add(scanResult.SSID +" - "+ scanResult.frequency +" - "+ scanResult.level);
                                deviceList.add(WifiInfo(scanResult.SSID))
                                Log.d(
                                    "myWifiList",
                                    "onPermissionGranted: " + scanResult.SSID + " - " + scanResult.frequency + " - " + scanResult.level
                                )
                            }
                            wifiDeviceList.adapter = WifiAdapter(context, deviceList)
                        }
                    }

                    override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {}
                    override fun onPermissionRationaleShouldBeShown(
                        permissionRequest: PermissionRequest,
                        permissionToken: PermissionToken
                    ) {
                    }
                }).check()
        }
    }
}