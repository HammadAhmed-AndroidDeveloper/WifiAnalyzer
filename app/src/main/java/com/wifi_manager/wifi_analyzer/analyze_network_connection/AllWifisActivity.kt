package com.wifi_manager.wifi_analyzer.analyze_network_connection

import android.Manifest
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wifi_manager.wifi_analyzer.analyze_network_connection.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class AllWifisActivity : AppCompatActivity() {

    private var wifiList: RecyclerView? = null
    private var wifiManager: WifiManager? = null
    var receiverWifi: WifiReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_wifis)

        wifiList = findViewById(R.id.wifiList)
        wifiList?.layoutManager = LinearLayoutManager(this)
        wifiList?.setHasFixedSize(true)
        val buttonScan = findViewById<Button>(R.id.scanBtn)
        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        if (!wifiManager!!.isWifiEnabled) {
            Toast.makeText(applicationContext, "Turning WiFi ON...", Toast.LENGTH_LONG).show()
            wifiManager!!.isWifiEnabled = true
        }
        buttonScan.setOnClickListener { v: View? -> wifi }
    }

    override fun onPostResume() {
        super.onPostResume()
        receiverWifi = WifiReceiver(wifiManager!!, wifiList!!)
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(receiverWifi, intentFilter)
        wifi
    }

    private val wifi: Unit
        get() {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        wifiManager!!.startScan()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest,
                        token: PermissionToken
                    ) {
                    }
                }).check()
        }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiverWifi)
    }
}