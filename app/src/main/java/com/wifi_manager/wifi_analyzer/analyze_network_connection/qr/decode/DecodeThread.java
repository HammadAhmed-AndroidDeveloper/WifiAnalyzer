package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.decode;

import android.os.Handler;
import android.os.Looper;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.ScanQRCodeActivity;

import java.util.concurrent.CountDownLatch;

final class DecodeThread extends Thread {
    private final ScanQRCodeActivity activity;
    private Handler handler;
    private final CountDownLatch countDown;

    public DecodeThread(ScanQRCodeActivity activity) {
        this.activity = activity;
        countDown = new CountDownLatch(1);
    }

    public Handler getHandler() {
        try {
            countDown.await();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new Decoder(activity);
        countDown.countDown();
        Looper.loop();
    }
}
