package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.camera;


import android.content.Context;
import android.hardware.Camera;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.utils.QRUtils;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

final class ConfiguringCamera {
    private static final int ZOOM = 10;

    private static final Pattern PATTERN = Pattern.compile(",");

    private Camera.Size cameraResolutionSize;
    private Camera.Size pictureResolutionSize;
    private final Context mContext;

    public ConfiguringCamera(Context context) {
        this.mContext = context;
    }

    public void initCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        cameraResolutionSize = closeSizes(QRUtils.getScreenWidth(mContext), QRUtils.getScreenHeight(mContext), parameters.getSupportedPreviewSizes());
        pictureResolutionSize = closeSizes(QRUtils.getScreenWidth(mContext), QRUtils.getScreenHeight(mContext), parameters.getSupportedPictureSizes());
    }

    public void setParams(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(cameraResolutionSize.width, cameraResolutionSize.height);
        parameters.setPictureSize(pictureResolutionSize.width, pictureResolutionSize.height);
        setZoom(parameters);
        camera.setDisplayOrientation(90);
        camera.setParameters(parameters);
    }

    Camera.Size getCameraResolution() {
        return cameraResolutionSize;
    }

    private static int setZoomLevel(CharSequence vals, int zoom_) {
        int temp = 0;
        for (String stringValue : PATTERN.split(vals)) {
            stringValue = stringValue.trim();
            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException e) {
                return zoom_;
            }
            int tenValue = (int) (10.0 * value);
            if (Math.abs(zoom_ - value) < Math.abs(zoom_ - temp)) {
                temp = tenValue;
            }
        }
        return temp;
    }

    private void setZoom(Camera.Parameters parameters) {

        String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
            return;
        }

        int temp = ZOOM;

        String maxZoomString = parameters.get("max-zoom");
        if (maxZoomString != null) {
            try {
                int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
                if (temp > tenMaxZoom) {
                    temp = tenMaxZoom;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
        if (takingPictureZoomMaxString != null) {
            try {
                int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                if (temp > tenMaxZoom) {
                    temp = tenMaxZoom;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        String mot = parameters.get("mot-zoom-values");
        if (mot != null) {
            temp = setZoomLevel(mot, temp);
        }

        String motZoom = parameters.get("mot-zoom-step");
        if (motZoom != null) {
            try {
                double motZoomStep = Double.parseDouble(motZoom.trim());
                int tenZoomStep = (int) (10.0 * motZoomStep);
                if (tenZoomStep > 1) {
                    temp -= temp % tenZoomStep;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (parameters.isZoomSupported()) {
            parameters.setZoom(parameters.getMaxZoom() / 10);
        }
    }

    private Camera.Size closeSizes(int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {
        preSizeList.sort(new SizeEval(surfaceWidth, surfaceHeight));
        return preSizeList.get(0);
    }

    private static class SizeEval implements Comparator<Camera.Size> {

        private final int width;
        private final int height;
        private final float ratio;

        SizeEval(int width, int height) {
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
