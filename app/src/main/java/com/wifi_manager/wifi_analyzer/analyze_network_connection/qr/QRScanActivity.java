package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.R;

import java.util.List;

public class QRScanActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QR_SCAN = 151;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        Button button = findViewById(R.id.button_start_scan);
        button.setOnClickListener(v -> {
            Intent i = new Intent(QRScanActivity.this, ScanQRCodeActivity.class);
            startActivityForResult(i, REQUEST_CODE_QR_SCAN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {

            if (data == null)
                return;
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(QRScanActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return;
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            AlertDialog alertDialog = new AlertDialog.Builder(QRScanActivity.this).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    (dialog, which) -> {
                        connectToWifi(result);
                        dialog.dismiss();
                    });
            alertDialog.show();

        }
    }

    private void connectToWifi(String result) {

        String ssid = null;
        String password = null;

        if (result.contains("WIFI:S:") && result.contains(";T:") &&
                (result.contains("WPA") || result.contains("WPA2")) && result.contains(";P:")
        ) {
            String startDelimiter = "S:";
            String endDelimiter = ";T:";
            int nameStartIndex = result.indexOf(startDelimiter) + 1;
            int nameEndIndex = result.indexOf(endDelimiter);

            int passwordStartIndex = result.indexOf(";P:") + 2;
            int passwordEndIndex = result.indexOf(";H:");

            if (nameStartIndex != -1 && nameEndIndex != -1 && nameStartIndex < nameEndIndex) {
                String extractedString = result.substring(nameStartIndex, nameEndIndex);
                extractedString = extractedString.replace(":", "");
                ssid = extractedString;
            }

            if (passwordStartIndex != -1 && passwordEndIndex != -1 && passwordStartIndex < passwordEndIndex) {
                String extractedPassword = result.substring(passwordStartIndex, passwordEndIndex);
                extractedPassword = extractedPassword.replace(":", "");
                password = extractedPassword;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                connectToWifi(ssid, password);
            } else {
                ConnectToNetworkWPA(ssid, password);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void connectToWifi(String ssid, String password) {
        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
        builder.setSsid(ssid);
        builder.setWpa2Passphrase(password);

        WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();

        NetworkRequest.Builder networkRequestBuilder1 = new NetworkRequest.Builder();
        networkRequestBuilder1.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        networkRequestBuilder1.setNetworkSpecifier(wifiNetworkSpecifier);

        NetworkRequest nr = networkRequestBuilder1.build();
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        ConnectivityManager.NetworkCallback networkCallback = new
                ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        super.onAvailable(network);
                        cm.bindProcessToNetwork(network);
                    }
                };
        cm.requestNetwork(nr, networkCallback);
    }

    public boolean ConnectToNetworkWPA(String networkSSID, String password) {
        try {
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";

            conf.preSharedKey = "\"" + password + "\"";

            conf.status = WifiConfiguration.Status.ENABLED;
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.addNetwork(conf);

            Log.d("qrCodeResult", conf.SSID + " " + conf.preSharedKey);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                for (WifiConfiguration i : list) {
                    if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(i.networkId, true);
                        wifiManager.reconnect();
                        Log.d("qrCodeResult", "wifi info " + i.SSID + " " + conf.preSharedKey);
                        break;
                    }
                }

                //WiFi Connection success, return true
                return true;
            } else {
                Toast.makeText(this, "Unable to Connect!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception ex) {
            Log.d("qrCodeResult", "ConnectToNetworkWPA: exception " + ex.getMessage());
            return false;
        }
    }
}