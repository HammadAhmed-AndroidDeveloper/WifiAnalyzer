package com.wifi_manager.wifi_analyzer.analyze_network_connection.wifi_speed_test.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class PingTester extends Thread {

    String server = "";
    int count;
    double instantValue = 0;
    double avg = 0.0;
    boolean finished = false;
    public PingTester(String ip, int counts) {
        this.server = ip;
        this.count = counts;
    }

    public double getAvg() {
        return avg;
    }

    public double getInstantValue() {
        return instantValue;
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public void run() {
        try {
            ProcessBuilder ps = new ProcessBuilder("ping", "-c " + count, this.server);

            ps.redirectErrorStream(true);
            Process pr = ps.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("icmp_seq")) {
                    instantValue = Double.parseDouble(line.split(" ")[line.split(" ").length - 2].replace("time=", ""));
                }
                if (line.startsWith("rtt ")) {
                    avg = Double.parseDouble(line.split("/")[4]);
                    break;
                }
                if (line.contains("Unreachable") || line.contains("Unknown") || line.contains("%100 packet loss")) {
                    return;
                }
            }
            pr.waitFor();
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        finished = true;
    }

}
