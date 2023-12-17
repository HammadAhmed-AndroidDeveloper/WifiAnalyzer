package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.decode;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.R;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.ScanQRCodeActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

final class Decoder extends Handler {

    private final ScanQRCodeActivity activity;
    private final QRCodeReader qrReader;
    private final Map<DecodeHintType, Object> hints;
    private byte[] data;

    public Decoder(ScanQRCodeActivity activity) {
        this.activity = activity;
        qrReader = new QRCodeReader();
        hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.decode) {
            decode((byte[]) message.obj, message.arg1, message.arg2);
        } else if (message.what == R.id.quit) {
            Looper looper = Looper.myLooper();
            if (null != looper) {
                looper.quit();
            }
        }
    }

    private void decode(byte[] data, int width, int height) {
        if (null == this.data) {
            this.data = new byte[width * height];
        } else {
            if (this.data.length < width * height) {
                this.data = new byte[width * height];
            }
        }
        Arrays.fill(this.data, (byte) 0);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x + y * width >= data.length) {
                    break;
                }
                this.data[x * height + height - y - 1] = data[x + y * width];
            }
        }
        int tmp = width;
        width = height;
        height = tmp;

        Result rawResult = null;
        try {
            PlanarYUVLuminanceSource source =
                    new PlanarYUVLuminanceSource(this.data, width, height, 0, 0, width, height, false);
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            rawResult = qrReader.decode(bitmap1, hints);
        } catch (ReaderException e) {
            e.printStackTrace();
        } finally {
            qrReader.reset();
        }

        if (rawResult != null) {
            Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
            message.sendToTarget();
        } else {
            Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
            message.sendToTarget();
        }
    }
}