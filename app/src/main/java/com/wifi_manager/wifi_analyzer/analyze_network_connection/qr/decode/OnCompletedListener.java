package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.decode;

import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

public final class OnCompletedListener implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener, Runnable {

    private final AppCompatActivity mActivityToFinish;

    public OnCompletedListener(AppCompatActivity activityToFinish) {
        this.mActivityToFinish = activityToFinish;
    }

    public void onCancel(DialogInterface dialogInterface) {
        run();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        run();
    }

    public void run() {
        mActivityToFinish.finish();
    }
}
