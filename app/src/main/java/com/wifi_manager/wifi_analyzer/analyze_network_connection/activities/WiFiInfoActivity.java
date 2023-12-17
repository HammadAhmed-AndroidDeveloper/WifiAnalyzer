package com.wifi_manager.wifi_analyzer.analyze_network_connection.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class WiFiInfoActivity extends AppCompatActivity {
    TextView name, ip, macAddress, speedTV, dns1TV, dns2TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_info);

        name = findViewById(R.id.wifiName);
        ip = findViewById(R.id.ipAddress);
        macAddress = findViewById(R.id.macAddress);
        dns1TV = findViewById(R.id.dns1);
        speedTV = findViewById(R.id.speed);
        dns2TV = findViewById(R.id.dns2);

        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

                        WifiInfo wInfo = wifiManager.getConnectionInfo();

                        String ipAddress = Formatter.formatIpAddress(wInfo.getIpAddress());
                        int linkSpeed = wInfo.getLinkSpeed();
                        String ssid = wInfo.getSSID();

                        String mac = wInfo.getMacAddress();

                        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

                        int dns1 = dhcpInfo.dns1;
                        int dns2 = dhcpInfo.dns2;

                        name.setText(ssid + "");
                        ip.setText(ipAddress + "");
                        macAddress.setText(mac + "");
                        speedTV.setText(linkSpeed + "");
                        dns1TV.setText(dns1 + "");
                        dns2TV.setText(dns2 + "");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
    }
}