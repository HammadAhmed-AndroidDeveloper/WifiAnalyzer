package com.wifi_manager.wifi_analyzer.analyze_network_connection.wifi_speed_test.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpUploadTest extends Thread {

    public String url = "";
    static int uploadedKByte = 0;
    double uploadElapsedTime = 0;
    boolean finished = false;
    double elapsedTime = 0;
    double uploadingRate = 0.0;
    long startTime;

    public HttpUploadTest(String url) {
        this.url = url;
    }

    private double round(double value) {
        try {
            BigDecimal bd;
            try {
                bd = new BigDecimal(value);
            } catch (Exception ex) {
                return 0.0;
            }
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0D;
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public double getUploadRate() {
        try {
            BigDecimal bd = new BigDecimal(uploadedKByte);
        } catch (Exception ex) {
            return 0.0;
        }

        if (uploadedKByte >= 0) {
            long now = System.currentTimeMillis();
            elapsedTime = (now - startTime) / 1000.0;
            return round((Double) (((uploadedKByte / 1000.0) * 8) / elapsedTime));
        } else {
            return 0.0;
        }
    }

    public double getUploadingRate() {
        return round(uploadingRate);
    }

    @Override
    public void run() {
        try {
            URL url = new URL(this.url);
            uploadedKByte = 0;
            startTime = System.currentTimeMillis();

            ExecutorService executor = Executors.newFixedThreadPool(4);
            for (int i = 0; i < 4; i++) {
                executor.execute(new HandlerUpload(url));
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

            long now = System.currentTimeMillis();
            uploadElapsedTime = (now - startTime) / 1000.0;
            uploadingRate = (Double) (((uploadedKByte / 1000.0) * 8) / uploadElapsedTime);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        finished = true;
    }
}


