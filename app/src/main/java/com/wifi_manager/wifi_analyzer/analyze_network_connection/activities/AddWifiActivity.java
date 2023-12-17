package com.wifi_manager.wifi_analyzer.analyze_network_connection.activities;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.R;

public class AddWifiActivity extends AppCompatActivity {

    EditText networkName, networkPassword;
    Spinner securityType;
    String security = "WEP";
    Button addNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi);

        networkName = findViewById(R.id.networkName);
        networkPassword = findViewById(R.id.networkPassword);
        securityType = findViewById(R.id.securityType);
        addNetwork = findViewById(R.id.addNetwork);

        String[] networkSecurityTypes = new String[]{"WEP", "WPA"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, networkSecurityTypes);
        securityType.setAdapter(adapter);

        securityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                security = (String) adapter.getItem(position);
                Toast.makeText(AddWifiActivity.this, security, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addNetwork.setOnClickListener(v -> {
            String name = networkName.getText().toString();
            String password = networkPassword.getText().toString();
            addNewWifiNetwork(AddWifiActivity.this, name, password, WifiConfiguration.SECURITY_TYPE_WEP);
        });
    }

    private void addWifiNetwork(String networkSSID, String networkPassword, String securityType) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + networkSSID + "\"";

        switch (securityType) {
            case "WEP":
                wifiConfig.wepKeys[0] = "\"" + networkPassword + "\"";
                wifiConfig.wepTxKeyIndex = 0;
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":
                wifiConfig.preSharedKey = "\"" + networkPassword + "\"";
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                break;
            case "EAP":
                // Handle EAP configuration as per your requirements
                // Set the appropriate security settings for EAP
                // E.g., wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
                break;
            default:
                // For open networks without security
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
        }

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        int networkId = wifiManager.addNetwork(wifiConfig);

        if (networkId != -1) {
            // Enable the newly added network
            wifiManager.enableNetwork(networkId, true);
            // Connect to the network
            wifiManager.reconnect();

        }
        Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show();
    }

    public static void addNewWifiNetwork(Context context, String ssid, String password, int securityType) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = ssid;
        wifiConfiguration.preSharedKey = password;

        switch (securityType) {
            case WifiConfiguration.SECURITY_TYPE_OPEN:
                break;
            case WifiConfiguration.SECURITY_TYPE_WEP:
                wifiConfiguration.wepKeys[0] = password;
                wifiConfiguration.wepTxKeyIndex = 0;
                break;
            case WifiConfiguration.SECURITY_TYPE_EAP_WPA3_ENTERPRISE:
                wifiConfiguration.preSharedKey = password;
                break;
            default:
                throw new IllegalArgumentException("Invalid security type: " + securityType);
        }
        int networkId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.reconnect();
    }


}