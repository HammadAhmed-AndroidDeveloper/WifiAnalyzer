package com.wifi_manager.wifi_analyzer.analyze_network_connection.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.AllWifisActivity;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.R;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.QRScanActivity;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.wifi_speed_test.WifiSpeedTestingActivity;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RelativeLayout devicesInfo, AllWifis, hotspot, passwordGenerator, wifiSpeedTester, wifiInfoBtn, addWifi, generateQRCodeWifi;
    ImageView routerBtn;
    TextView connectedWiFi, isConnected;
    LinearLayout toolbar;
    ImageView drawerHandler;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private final String TAG = "MainActivity";

    int adClick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        devicesInfo = findViewById(R.id.deviceInfo);
        addWifi = findViewById(R.id.addWifi);
        hotspot = findViewById(R.id.hotspot);
        AllWifis = findViewById(R.id.AllWifis);
        wifiInfoBtn = findViewById(R.id.wifiInfo);
        wifiSpeedTester = findViewById(R.id.wifiSpeedTester);
        passwordGenerator = findViewById(R.id.passwordGenerator);
        connectedWiFi = findViewById(R.id.connectedWiFi);
        isConnected = findViewById(R.id.isConnected);
        generateQRCodeWifi = findViewById(R.id.generateQRCodeWifi);
        routerBtn = findViewById(R.id.routerBtn);
        toolbar = findViewById(R.id.toolbar);
        drawerHandler = findViewById(R.id.drawerHandler);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerHandler.setOnClickListener(view -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.openDrawer(GravityCompat.START);
            else drawerLayout.closeDrawer(GravityCompat.END);
        });
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();

        if (ssid != null) {
            isConnected.setText("Connected");
        }
        connectedWiFi.setText(ssid + "");


        devicesInfo.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ConnectedDevicesActivity.class));
        });
        AllWifis.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AllWifisActivity.class));
        });

        wifiInfoBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, WiFiInfoActivity.class));
        });
        routerBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WifiRouterActivity.class)));

        passwordGenerator.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PasswordGeneratorActivity.class));
        });

        generateQRCodeWifi.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, GenerateQRCodeForWifi.class));
        });

        wifiSpeedTester.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, WifiSpeedTestingActivity.class));
        });


        addWifi.setOnClickListener(v -> {
            Dexter.withContext(MainActivity.this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    startActivity(new Intent(MainActivity.this, QRScanActivity.class));
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                }
            }).check();
        });
        hotspot.setOnClickListener(v -> {
            openHotspotSettings();
        });
    }


    public void openHotspotSettings() {
        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        actionBarDrawerToggle.onOptionsItemSelected(item);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            String shareMessage = "\nHey check this app, it's amazing\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rateUs() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void privacyPolicy() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.share) {
            shareApp();
        } else if (item.getItemId() == R.id.rateUs) {
            rateUs();
        } else if (item.getItemId() == R.id.privacyPolicy) {
            privacyPolicy();
        }
        return true;
    }
}
