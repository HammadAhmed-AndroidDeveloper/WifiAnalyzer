package com.blikoon.qrcodescanner.camera;


import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import com.blikoon.qrcodescanner.utils.ScreenUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

final class CameraConfigurationManager {
    private static final String TAG = CameraConfigurationManager.class.getName();
    private static final int TEN_DESIRED_ZOOM = 10;
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    private Camera.Size mCameraResolution;
    private Camera.Size mPictureResolution;
    private final Context mContext;

    CameraConfigurationManager(Context context) {
        this.mContext = context;
    }

    void initFromCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        mCameraResolution = findCloselySize(ScreenUtils.getScreenWidth(mContext), ScreenUtils.getScreenHeight(mContext),
                parameters.getSupportedPreviewSizes());
        Log.e(TAG, "Setting preview size: " + mCameraResolution.width + "-" + mCameraResolution.height);
        mPictureResolution = findCloselySize(ScreenUtils.getScreenWidth(mContext),
                ScreenUtils.getScreenHeight(mContext), parameters.getSupportedPictureSizes());
        Log.e(TAG, "Setting picture size: " + mPictureResolution.width + "-" + mPictureResolution.height);
    }

    void setDesiredCameraParameters(Camera camera) {

        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(mCameraResolution.width, mCameraResolution.height);
        parameters.setPictureSize(mPictureResolution.width, mPictureResolution.height);
        setZoom(parameters);
        camera.setDisplayOrientation(90);
        camera.setParameters(parameters);
    }

    Camera.Size getCameraResolution() {
        return mCameraResolution;
    }


    private static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
        int tenBestValue = 0;
        for (String stringValue : COMMA_PATTERN.split(stringValues)) {
            stringValue = stringValue.trim();
            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException nfe) {
                return tenDesiredZoom;
            }
            int tenValue = (int) (10.0 * value);
            if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }
        return tenBestValue;
    }

    private void setZoom(Camera.Parameters parameters) {

        String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
            return;
        }

        int tenDesiredZoom = TEN_DESIRED_ZOOM;

        String maxZoomString = parameters.get("max-zoom");
        if (maxZoomString != null) {
            try {
                int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
                Log.e(TAG, "Bad max-zoom: " + maxZoomString);
            }
        }

        String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
        if (takingPictureZoomMaxString != null) {
            try {
                int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
                Log.e(TAG, "Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
            }
        }

        String motZoomValuesString = parameters.get("mot-zoom-values");
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
        }

        String motZoomStepString = parameters.get("mot-zoom-step");
        if (motZoomStepString != null) {
            try {
                double motZoomStep = Double.parseDouble(motZoomStepString.trim());
                int tenZoomStep = (int) (10.0 * motZoomStep);
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                }
            } catch (NumberFormatException nfe) {
          nfe.printStackTrace();
            }
        }

        if (parameters.isZoomSupported()) {
            Log.e(TAG, "max-zoom:" + parameters.getMaxZoom());
            parameters.setZoom(parameters.getMaxZoom() / 10);
        } else {
            Log.e(TAG, "Unsupported zoom.");
        }

    }

    private Camera.Size findCloselySize(int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {

        Collections.sort(preSizeList, new SizeComparator(surfaceWidth, surfaceHeight));
        return preSizeList.get(0);
    }

    private static class SizeComparator implements Comparator<Camera.Size> {

        private final int width;
        private final int height;
        private final float ratio;

        SizeComparator(int width, int height) {
            if (width < height) {
                this.width = height;
                this.height = width;
            } else {
                this.width = width;
                this.height = height;
            }
            this.ratio = (float) this.height / this.width;
        }

        @Override
        public int compare(Camera.Size size1, Camera.Size size2) {
            int width1 = size1.width;
            int height1 = size1.height;
            int width2 = size2.width;
            int height2 = size2.height;

            float ratio1 = Math.abs((float) height1 / width1 - ratio);
            float ratio2 = Math.abs((float) height2 / width2 - ratio);
            int result = Float.compare(ratio1, ratio2);
            if (result != 0) {
                return result;
            } else {
                int minGap1 = Math.abs(width - width1) + Math.abs(height - height1);
                int minGap2 = Math.abs(width - width2) + Math.abs(height - height2);
                return minGap1 - minGap2;
            }
        }
    }
}