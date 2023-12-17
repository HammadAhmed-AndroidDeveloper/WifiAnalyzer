package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

final class CameraPreviewListener implements Camera.PreviewCallback {
    private final ConfiguringCamera cc;
    private Handler handler;
    private int msg;

    CameraPreviewListener(ConfiguringCamera configManager) {
        this.cc = configManager;
    }

    void setHandler(Handler previewHandler, int previewMessage) {
        this.handler = previewHandler;
        this.msg = previewMessage;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Size cameraResolution = cc.getCameraResolution();
        if (handler != null) {
            Message message =
                    handler.obtainMessage(msg, cameraResolution.width, cameraResolution.height, data);
            message.sendToTarget();
            handler = null;
        }
    }
}