package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.decode;

import com.google.zxing.Result;

public interface DecodingListener {

    void onSuccsses(Result result);

    void onFailure(int type, String reason);
}
