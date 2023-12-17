package com.blikoon.qrcodescanner.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

public final class CameraManager {

    private static CameraManager sCameraManager;

    private final CameraConfigurationManager mConfigManager;
    private Camera mCamera;
    private boolean mInitialized;
    private boolean mPreviewing;

    private final PreviewCallback mPreviewCallback;
    private final AutoFocusCallback mAutoFocusCallback;
    public static void init(Context context) {
        if (sCameraManager == null) {
            sCameraManager = new CameraManager(context);
        }
    }

    public static CameraManager get() {
        return sCameraManager;
    }

    private CameraManager(Context context) {
        this.mConfigManager = new CameraConfigurationManager(context);
        mPreviewCallback = new PreviewCallback(mConfigManager);
        mAutoFocusCallback = new AutoFocusCallback();
    }

    public void openDriver(SurfaceHolder holder) throws IOException {
        if (mCamera == null) {
            mCamera = Camera.open();
            if (mCamera == null) {
                throw new IOException();
            }
            mCamera.setPreviewDisplay(holder);

            if (!mInitialized) {
                mInitialized = true;
                mConfigManager.initFromCameraParameters(mCamera);
            }
            mConfigManager.setDesiredCameraParameters(mCamera);
        }
    }


    public boolean setFlashLight(boolean open) {
        if (mCamera == null) {
            return false;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return false;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        if (null == flashModes || 0 == flashModes.size()) {
            return false;
        }
        String flashMode = parameters.getFlashMode();
        if (open) {
            if (Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
                return true;
            }
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                return true;
            } else {
                return false;
            }
        } else {
            if (Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
                return true;
            }
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                return true;
            } else
                return false;
        }
    }

    /**
     * Closes the camera driver if still in use.
     */
    public void closeDriver() {
        if (mCamera != null) {
            mCamera.release();
            mInitialized = false;
            mPreviewing = false;
            mCamera = null;
        }
    }

    /**
     * Asks the mCamera hardware to begin drawing preview frames to the screen.
     */
    public void startPreview() {
        if (mCamera != null && !mPreviewing) {
            mCamera.startPreview();
            mPreviewing = true;
        }
    }

    /**
     * Tells the mCamera to stop drawing preview frames.
     */
    public void stopPreview() {
        if (mCamera != null && mPreviewing) {
            mCamera.stopPreview();
            mPreviewCallback.setHandler(null, 0);
            mAutoFocusCallback.setHandler(null, 0);
            mPreviewing = false;
        }
    }

    /**
     * A single preview frame will be returned to the handler supplied. The data will arrive as byte[] in the
     * message.obj field, with width and height encoded as message.arg1 and message.arg2, respectively.
     *
     * @param handler The handler to send the message to.
     * @param message The what field of the message to be sent.
     */
    public void requestPreviewFrame(Handler handler, int message) {
        if (mCamera != null && mPreviewing) {
            mPreviewCallback.setHandler(handler, message);
            mCamera.setOneShotPreviewCallback(mPreviewCallback);
        }
    }

    /**
     * Asks the mCamera hardware to perform an autofocus.
     *
     * @param handler The Handler to notify when the autofocus completes.
     * @param message The message to deliver.
     */
    public void requestAutoFocus(Handler handler, int message) {
        if (mCamera != null && mPreviewing) {
            mAutoFocusCallback.setHandler(handler, message);
            // Log.d(TAG, "Requesting auto-focus callback");
            mCamera.autoFocus(mAutoFocusCallback);
        }
    }
}
