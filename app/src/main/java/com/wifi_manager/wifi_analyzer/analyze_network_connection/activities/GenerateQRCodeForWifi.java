package com.wifi_manager.wifi_analyzer.analyze_network_connection.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class GenerateQRCodeForWifi extends AppCompatActivity {

    ImageView wifiQRCodeImg;
    Button generateQRCode;
    EditText networkName, networkPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode_for_wifi);

        networkName = findViewById(R.id.networkName);
        networkPassword = findViewById(R.id.networkPassword);
        wifiQRCodeImg = findViewById(R.id.wifiQRCode);

        generateQRCode = findViewById(R.id.generateQRCode);

        generateQRCode.setOnClickListener(v -> {
            try {
                String name = networkName.getText().toString();
                String password = networkPassword.getText().toString();
                wifiQRCodeImg.setImageBitmap(generateQRCode(name, password));
                wifiQRCodeImg.setVisibility(View.VISIBLE);
            } catch (WriterException e) {
                Toast.makeText(GenerateQRCodeForWifi.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("onError", "onClick: " + e.getMessage());
            }
        });
    }

    public  Bitmap generateQRCode(String ssid, String password) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        String text = ssid + ":" + password;
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512);
        Bitmap bitmap = Bitmap.createBitmap(matrix.getWidth(), matrix.getHeight(), Bitmap.Config.ARGB_8888);
        for (int x = 0; x < matrix.getWidth(); x++) {
            for (int y = 0; y < matrix.getHeight(); y++) {
                bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }
}