package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.decode;


import android.graphics.Bitmap;
import android.text.TextUtils;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.utils.QRCodeBytesHandler;
import com.google.zxing.Result;

public class DecodeImageThread implements Runnable {
    private static final int MAX_PICTURE_PIXEL = 256;
    private byte[] mData;
    private int mWidth;
    private int mHeight;
    private String mImgPath;
    private DecodingListener mCallback;

    public DecodeImageThread(String imgPath, DecodingListener callback) {
        this.mImgPath = imgPath;
        this.mCallback = callback;
    }

    @Override
    public void run() {
        if (null == mData) {
            if (!TextUtils.isEmpty(mImgPath)) {
                Bitmap bitmap = QRCodeBytesHandler.deocodeBitmapFromFile(mImgPath, MAX_PICTURE_PIXEL, MAX_PICTURE_PIXEL);
                this.mData = QRCodeBytesHandler.getBytes(bitmap.getWidth(), bitmap.getHeight(), bitmap);
                this.mWidth = bitmap.getWidth();
                this.mHeight = bitmap.getHeight();
            }
        }

        if (mData == null || mData.length == 0 || mWidth == 0 || mHeight == 0) {
            if (null != mCallback) {
                mCallback.onFailure(0, "No image data");
            }
            return;
        }

        final Result result = QRCodeBytesHandler.decodeImage(mData, mWidth, mHeight);

        if (null != mCallback) {
            if (null != result) {
                mCallback.onSuccsses(result);
            } else {
                mCallback.onFailure(0, "Decode image failed.");
            }
        }
    }
}
