package com.wifi_manager.wifi_analyzer.analyze_network_connection.wifi_speed_test.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SpeedTestHandler extends Thread {

    HashMap<Integer, String> keys = new HashMap<>();
    HashMap<Integer, List<String>> values = new HashMap<>();
    double lat = 0.0;
    double longn = 0.0;
    boolean finished = false;
    public HashMap<Integer, String> getKeys() {
        return keys;
    }
    public HashMap<Integer, List<String>> getValues() {
        return values;
    }
    public double getLat() {
        return lat;
    }

    public double getLongn() {
        return longn;
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public void run() {
        try {
            URL url = new URL("https://www.speedtest.net/speedtest-config.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int code = urlConnection.getResponseCode();

            if (code == 200) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));

                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.contains("isp=")) {
                        continue;
                    }
                    lat = Double.parseDouble(line.split("lat=\"")[1].split(" ")[0].replace("\"", ""));
                    longn = Double.parseDouble(line.split("lon=\"")[1].split(" ")[0].replace("\"", ""));
                    break;
                }

                br.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        String uploadAddress = "";
        String name = "";
        String country = "";
        String cc = "";
        String sponsor = "";
        String lat = "";
        String lon = "";
        String host = "";

        int count = 0;
        try {
            URL url = new URL("https://www.speedtest.net/speedtest-servers-static.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int code = urlConnection.getResponseCode();

            if (code == 200) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                urlConnection.getInputStream()));

                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("<server url")) {
                        uploadAddress = line.split("server url=\"")[1].split("\"")[0];
                        lat = line.split("lat=\"")[1].split("\"")[0];
                        lon = line.split("lon=\"")[1].split("\"")[0];
                        name = line.split("name=\"")[1].split("\"")[0];
                        country = line.split("country=\"")[1].split("\"")[0];
                        cc = line.split("cc=\"")[1].split("\"")[0];
                        sponsor = line.split("sponsor=\"")[1].split("\"")[0];
                        host = line.split("host=\"")[1].split("\"")[0];

                        List<String> ls = Arrays.asList(lat, lon, name, country, cc, sponsor, host);
                        keys.put(count, uploadAddress);
                        values.put(count, ls);
                        count++;
                    }
                }

                br.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        finished = true;
    }
}
