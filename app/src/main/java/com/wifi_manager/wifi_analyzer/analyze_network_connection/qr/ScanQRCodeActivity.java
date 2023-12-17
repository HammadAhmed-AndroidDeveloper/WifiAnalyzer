package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.R;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.camera.CameraManager;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.decode.CapturingActivity;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.decode.DecodeImageThread;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.decode.DecodingListener;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.decode.DialogsBuilder;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.decode.InactiveTiming;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.view.QrCodeFinderView;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ScanQRCodeActivity extends AppCompatActivity implements Callback, OnClickListener {

    private static final int IMAGES_REQUEST = 0;
    private static final int REQUEST_PICTURE = 1;
    private CapturingActivity ca;
    private boolean isSurfaceAvailable;
    private boolean isPermission;
    private InactiveTiming timeHandler;
    private QrCodeFinderView qrView;
    private SurfaceView sv;
    private View flashView;
    private final DialogsBuilder dBuilder = new DialogsBuilder();
    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;
    private MediaPlayer player;
    private boolean beep;
    private boolean isVibrating;
    private boolean isFlash = true;
    private ImageView flashImg;
    private TextView flashTV;
    private Executor executer;
    private final String RESULT = "com.blikoon.qrcodescanner.got_qr_scan_relult";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        TextView tvPic = (TextView) findViewById(R.id.qr_code_header_black_pic);
        flashImg = (ImageView) findViewById(R.id.qr_code_iv_flash_light);
        flashTV = (TextView) findViewById(R.id.qr_code_tv_flash_light);
        qrView = (QrCodeFinderView) findViewById(R.id.qr_code_view_finder);
        sv = (SurfaceView) findViewById(R.id.qr_code_preview_view);
        flashView = findViewById(R.id.qr_code_ll_flash_light);
        isSurfaceAvailable = false;
        flashImg.setOnClickListener(this);
        tvPic.setOnClickListener(this);

        CameraManager.init(this);
        timeHandler = new InactiveTiming(ScanQRCodeActivity.this);
        executer = Executors.newSingleThreadExecutor();
    }

    private void checkPermission() {
        boolean hasHardware = isCamera();
        if (hasHardware) {
            if (isPermission()) {
                findViewById(R.id.qr_code_view_background).setVisibility(View.VISIBLE);
                qrView.setVisibility(View.GONE);
                isPermission = false;
            } else {
                isPermission = true;
            }
        } else {
            isPermission = false;
            finish();
        }
    }

    private boolean isPermission() {
        return PackageManager.PERMISSION_GRANTED != getPackageManager().checkPermission("android.permission.CAMERA", getPackageName());
    }

    private void handleMain() {
        checkPermission();
        if (!isPermission) {
            dBuilder.permissionDialog(this);
            return;
        }
        SurfaceHolder sh = sv.getHolder();
        flashOff();
        if (isSurfaceAvailable) {
            initCamera(sh);
        } else {
            sh.addCallback(this);
            sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        beep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            beep = false;
        }
        beeps();
        isVibrating = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleMain();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ca != null) {
            ca.quitSynchronously();
            ca = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        if (null != timeHandler) {
            timeHandler.shutdown();
        }
        super.onDestroy();
    }


    public void handleDecode(Result result) {
        timeHandler.onActivity();
        playBeepSoundAndVibrate();
        if (null == result) {
            dBuilder.isNotREady(this, this::restartPreview);
        } else {
            handleResult(result.getText());

        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.qr_code_camera_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        } catch (RuntimeException re) {
            re.printStackTrace();
            dBuilder.permissionDialog(this);
            return;
        }
        qrView.setVisibility(View.VISIBLE);
        sv.setVisibility(View.VISIBLE);
        flashView.setVisibility(View.VISIBLE);
        findViewById(R.id.qr_code_view_background).setVisibility(View.GONE);
        if (ca == null) {
            ca = new CapturingActivity(this);
        }
    }

    private void restartPreview() {
        if (null != ca) {
            ca.restartPreviewAndDecode();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    private boolean isCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!isSurfaceAvailable) {
            isSurfaceAvailable = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isSurfaceAvailable = false;
    }

    public Handler getHandler() {
        return ca;
    }

    private void beeps() {
        if (beep && player == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnCompletionListener(mBeepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                player.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                player.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                player.prepare();
            } catch (IOException e) {
                player = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (beep && player != null) {
            player.start();
        }
        if (isVibrating) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final MediaPlayer.OnCompletionListener mBeepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.qr_code_iv_flash_light) {
            if (isFlash) {
                flashOn();
            } else {
                flashOff();
            }

        } else if (v.getId() == R.id.qr_code_header_black_pic) {
            if (isPermission()) {
                dBuilder.permissionDialog(this);
            } else {
                openGallery();
            }

        }

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGES_REQUEST);
    }

    private void flashOn() {
        isFlash = false;
        flashTV.setText(getString(R.string.qr_code_close_flash_light));
        flashImg.setBackgroundResource(R.drawable.ic_launcher_background);
        CameraManager.get().setFlashLight(true);
    }

    private void flashOff() {
        isFlash = true;
        flashTV.setText(getString(R.string.qr_code_open_flash_light));
        flashImg.setBackgroundResource(R.drawable.ic_launcher_foreground);
        CameraManager.get().setFlashLight(false);
    }

    private void handleResult(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            dBuilder.isNotREady(this, this::restartPreview);
        } else {
            Intent data = new Intent();
            data.putExtra(RESULT, resultString);
            setResult(Activity.RESULT_OK, data);
            finish();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_PICTURE -> finish();
            case IMAGES_REQUEST -> {
                Uri uri = data.getData();
                String imgPath = getPathFromUri(uri);
                if (imgPath != null && !TextUtils.isEmpty(imgPath) && null != executer) {
                    executer.execute(new DecodeImageThread(imgPath, mDecodingListener));
                }
            }
        }
    }

    public String getPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    private final DecodingListener mDecodingListener = new DecodingListener() {
        @Override
        public void onSuccsses(Result result) {
            Intent data = new Intent();
            data.putExtra(RESULT, result.getText());
            setResult(Activity.RESULT_OK, data);
            finish();
        }

        @Override
        public void onFailure(int type, String reason) {
            Intent data = new Intent();
            String ERROR_DECODING_IMAGE = "com.blikoon.qrcodescanner.error_decoding_image";
            data.putExtra(ERROR_DECODING_IMAGE, reason);
            setResult(Activity.RESULT_CANCELED, data);
            finish();
        }
    };
}