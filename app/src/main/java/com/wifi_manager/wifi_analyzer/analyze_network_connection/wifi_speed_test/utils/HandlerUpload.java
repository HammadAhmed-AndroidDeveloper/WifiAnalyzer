package com.wifi_manager.wifi_analyzer.analyze_network_connection.wifi_speed_test.utils;

import java.io.DataOutputStream;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

public class HandlerUpload extends Thread {

    private final URL url;

    public HandlerUpload(URL url) {
        this.url = url;
    }

    public void run() {
        try {
            byte[] buffer = new byte[150 * 1024];
            long startTime = System.currentTimeMillis();
            int timeout = 8;

            while (true) {

                try {
                    HttpsURLConnection conn = null;
                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
                    conn.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                    conn.connect();
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());


                    dos.write(buffer, 0, buffer.length);
                    dos.flush();

                    conn.getResponseCode();

                    HttpUploadTest.uploadedKByte += buffer.length / 1024.0;
                    long endTime = System.currentTimeMillis();
                    double uploadElapsedTime = (endTime - startTime) / 1000.0;
                    if (uploadElapsedTime >= timeout) {
                        break;
                    }

                    dos.close();
                    conn.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}