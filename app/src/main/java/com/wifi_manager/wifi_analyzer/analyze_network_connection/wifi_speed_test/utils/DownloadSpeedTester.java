package com.wifi_manager.wifi_analyzer.analyze_network_connection.wifi_speed_test.utils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class DownloadSpeedTester extends Thread {

    public String url = "";
    long timeOfStarting = 0;
    long timeOfEnding = 0;
    double downloadingTime = 0;
    int bytesTotallyDownloaded = 0;
    double downloadingRate = 0.0;
    boolean finished = false;
    double startingDownloadingRate = 0;
    int timeout = 8;

    HttpsURLConnection connection = null;

    public DownloadSpeedTester(String url) {
        this.url = url;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        try {
            BigDecimal bd;
            try {
                bd = new BigDecimal(value);
            } catch (Exception ex) {
                return 0.0;
            }
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } catch (Exception e) {
            return 0D;
        }
    }

    public double getDownloadingRateAtStart() {
        return startingDownloadingRate;
    }

    public void setDownloadRate(int d, double time) {
        if (d >= 0) {
            this.startingDownloadingRate = round((Double) (((d * 8) / (1000 * 1000)) / time), 2);
        } else {
            this.startingDownloadingRate = 0.0;
        }
    }

    public double getDownloadingRate() {
        return round(downloadingRate, 2);
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public void run() {
        try {
            URL url = null;
            bytesTotallyDownloaded = 0;
            int responseCode = 0;

            List<String> fileUrls = new ArrayList<>();
            fileUrls.add(this.url + "random4000x4000.jpg");
            fileUrls.add(this.url + "random3000x3000.jpg");

            timeOfStarting = System.currentTimeMillis();

            outer:
            for (String link : fileUrls) {
                try {
                    url = new URL(link);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
                    connection.setHostnameVerifier((hostname, session) -> true);
                    connection.connect();
                    responseCode = connection.getResponseCode();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    break outer;
                }

                try {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        byte[] buffer = new byte[10240];

                        InputStream inputStream = connection.getInputStream();
                        int len = 0;

                        while ((len = inputStream.read(buffer)) != -1) {
                            bytesTotallyDownloaded += len;
                            timeOfEnding = System.currentTimeMillis();
                            downloadingTime = (timeOfEnding - timeOfStarting) / 1000.0;
                            setDownloadRate(bytesTotallyDownloaded, downloadingTime);
                            if (downloadingTime >= timeout) {
                                break outer;
                            }
                        }

                        inputStream.close();
                        connection.disconnect();
                    } else {
                        System.out.println("Link not found...");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            timeOfEnding = System.currentTimeMillis();
            downloadingTime = (timeOfEnding - timeOfStarting) / 1000.0;
            downloadingRate = ((bytesTotallyDownloaded * 8) / (1000 * 1000.0)) / downloadingTime;

            finished = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
