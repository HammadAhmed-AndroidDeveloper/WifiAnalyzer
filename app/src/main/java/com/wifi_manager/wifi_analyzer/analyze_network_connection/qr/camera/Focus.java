package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

final class Focus implements Camera.AutoFocusCallback {
    private static final long INTERVAL = 1000L;

    private Handler handler;
    private int msg;

    public void setFocusHandler(Handler handler, int msg) {
        this.handler = handler;
        this.msg = msg;
    }

    public void onAutoFocus(boolean success, Camera camera) {
        if (handler != null) {
            Message message = handler.obtainMessage(msg, success);
            handler.sendMessageDelayed(message, INTERVAL);
            handler = null;
        }
    }

}
