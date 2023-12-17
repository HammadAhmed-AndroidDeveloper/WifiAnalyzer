package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Arrays;
import java.util.Hashtable;


public class QRCodeBytesHandler {
    private static byte[] yuvs;

    public static byte[] getBytes(int iW, int iH, Bitmap bmp) {
        int[] argb = new int[iW * iH];

        bmp.getPixels(argb, 0, iW, 0, 0, iW, iH);

        int requiredWidth = iW % 2 == 0 ? iW : iW + 1;
        int requiredHeight = iH % 2 == 0 ? iH : iH + 1;

        int byteLength = requiredWidth * requiredHeight * 3 / 2;
        if (yuvs == null || yuvs.length < byteLength) {
            yuvs = new byte[byteLength];
        } else {
            Arrays.fill(yuvs, (byte) 0);
        }

        encode(yuvs, argb, iW, iH);

        bmp.recycle();

        return yuvs;
    }

    private static void encode(byte[] b1, int[] argb, int width, int height) {
        final int frameSize = width * height;
        int Y, U, V;
        int yIndex = 0;
        int uvIndex = frameSize;

        int R, G, B;
        int argbIndex = 0;

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                R = (argb[argbIndex] & 0xff0000) >> 16;
                G = (argb[argbIndex] & 0xff00) >> 8;
                B = (argb[argbIndex] & 0xff);
                //
                argbIndex++;

                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                //
                Y = Math.max(0, Math.min(Y, 255));
                U = Math.max(0, Math.min(U, 255));
                V = Math.max(0, Math.min(V, 255));

                b1[yIndex++] = (byte) Y;
                if ((j % 2 == 0) && (i % 2 == 0)) {
                    b1[uvIndex++] = (byte) V;
                    b1[uvIndex++] = (byte) U;
                }
            }
        }
    }

    public static int calculate(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap deocodeBitmapFromFile(String imgPath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);

        options.inSampleSize = calculate(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);
    }
    public static Result decodeImage(byte[] data, int width, int height) {
        Result result = null;
        try {
            Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
            PlanarYUVLuminanceSource source =
                    new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);

            BinaryBitmap bitmap1 = new BinaryBitmap(new GlobalHistogramBinarizer(source));
            QRCodeReader reader2 = new QRCodeReader();
            result = reader2.decode(bitmap1, hints);
        } catch (ReaderException e) {
            e.printStackTrace();
        }
        return result;
    }
}
