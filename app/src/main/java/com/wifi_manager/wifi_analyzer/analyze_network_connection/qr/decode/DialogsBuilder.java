package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.decode;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.R;


public class DialogsBuilder {

    public void permissionDialog(Context context) {
        new AlertDialog.Builder(context).setTitle(R.string.qr_code_notification)
                .setMessage(R.string.qr_code_camera_not_open)
                .setPositiveButton(R.string.qr_code_positive_button_know, (dialog, which) -> dialog.dismiss()).show();
    }
    public void isNotREady(Context context, final RefreshCamera listener) {
        new AlertDialog.Builder(context).setTitle(R.string.qr_code_notification)
                .setMessage(R.string.qr_code_could_not_read_qr_code_from_scanner)
                .setPositiveButton(R.string.qc_code_close, (dialog, which) -> {
                    dialog.dismiss();
                    if (listener != null) {
                        listener.doRefresh();
                    }
                }).show();
    }
    public interface RefreshCamera {
        void doRefresh();
    }

}
