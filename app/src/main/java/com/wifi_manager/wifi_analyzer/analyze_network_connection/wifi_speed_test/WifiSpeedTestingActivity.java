package com.wifi_manager.wifi_analyzer.analyze_network_connection.wifi_speed_test;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.R;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.wifi_speed_test.utils.DownloadSpeedTester;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.wifi_speed_test.utils.SpeedTestHandler;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.wifi_speed_test.utils.HttpUploadTest;
import com.wifi_manager.wifi_analyzer.analyze_network_connection.wifi_speed_test.utils.PingTester;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class WifiSpeedTestingActivity extends AppCompatActivity {
    static int position = 0;
    static int lastPosition = 0;
    SpeedTestHandler handler = null;
    HashSet<String> tempBlackList;
    RotateAnimation rotateAnimation;

    @Override
    public void onResume() {
        super.onResume();

        try {
            handler = new SpeedTestHandler();
            handler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_speed_test);

        final Button startBtn = (Button) findViewById(R.id.startButton);
        final DecimalFormat dec = new DecimalFormat("#.##");
        startBtn.setText("Begin Test");

        tempBlackList = new HashSet<>();

        handler = new SpeedTestHandler();
        handler.start();

        final ImageView barImageView = (ImageView) findViewById(R.id.barImageView);
        final TextView pingTextView = (TextView) findViewById(R.id.pingTextView);
        final TextView downloadTextView = (TextView) findViewById(R.id.downloadTextView);
        final TextView uploadTextView = (TextView) findViewById(R.id.uploadTextView);

        startBtn.setOnClickListener(v -> {
            startBtn.setEnabled(false);

            if (handler == null) {
                handler = new SpeedTestHandler();
                handler.start();
            }

            try {
                new Thread(() -> {
                    runOnUiThread(() -> startBtn.setText("Selecting best server based on ping..."));

                    int timeCount = 600;
                    try {
                        while (!handler.isFinished()) {
                            timeCount--;
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ignored) {
                            }
                            if (timeCount <= 0) {
                                runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), "No Connection...", Toast.LENGTH_LONG).show();
                                    startBtn.setEnabled(true);
                                    startBtn.setTextSize(14);
                                    startBtn.setText("Restart");
                                });
                                handler = null;
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    HashMap<Integer, String> mapsOfKeys = handler.getKeys();
                    HashMap<Integer, List<String>> mapsOfValues = handler.getValues();
                    double _lat = handler.getLat();
                    double _logn = handler.getLongn();
                    double temp = 19349458;
                    double dist = 0.0;
                    int fsi = 0;
                    try {
                        for (int index : mapsOfKeys.keySet()) {
                            if (tempBlackList.contains(mapsOfValues.get(index).get(5))) {
                                continue;
                            }

                            Location src = new Location("Source");
                            src.setLatitude(_lat);
                            src.setLongitude(_logn);

                            List<String> ls = mapsOfValues.get(index);
                            Location dest = new Location("Dest");
                            dest.setLatitude(Double.parseDouble(ls.get(0)));
                            dest.setLongitude(Double.parseDouble(ls.get(1)));

                            double distance = src.distanceTo(dest);
                            if (temp > distance) {
                                temp = distance;
                                dist = distance;
                                fsi = index;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String testingAddress = mapsOfKeys.get(fsi).replace("http://", "https://");
                    final List<String> info = mapsOfValues.get(fsi);
                    final double distance = dist;

                    try {
                        if (info == null) {
                            runOnUiThread(() -> {
                                startBtn.setTextSize(12);
                                startBtn.setText("There was a problem in getting Host. Try again later.");
                            });
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(() -> {
                        startBtn.setTextSize(12);
                        startBtn.setText(String.format("Host Location: %s [Distance: %s km]", info.get(2), new DecimalFormat("#.##").format(distance / 1000)));
                    });

                    final LinearLayout chart = (LinearLayout) findViewById(R.id.chartPing);
                    XYSeriesRenderer xySeriesRenderer = new XYSeriesRenderer();
                    XYSeriesRenderer.FillOutsideLine pingFill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
                    pingFill.setColor(Color.parseColor("#FFFFFF"));

                    xySeriesRenderer.addFillOutsideLine(pingFill);
                    xySeriesRenderer.setDisplayChartValues(false);
                    xySeriesRenderer.setShowLegendItem(false);
                    xySeriesRenderer.setColor(Color.parseColor("#FFFFFF"));
                    xySeriesRenderer.setLineWidth(5);

                    final XYMultipleSeriesRenderer multiPingRenderer = new XYMultipleSeriesRenderer();
                    multiPingRenderer.setXLabels(0);
                    multiPingRenderer.setYLabels(0);
                    multiPingRenderer.setZoomEnabled(false);
                    multiPingRenderer.setXAxisColor(Color.parseColor("#647488"));
                    multiPingRenderer.setYAxisColor(Color.parseColor("#2F3C4C"));
                    multiPingRenderer.setPanEnabled(true, true);
                    multiPingRenderer.setZoomButtonsVisible(false);
                    multiPingRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                    multiPingRenderer.addSeriesRenderer(xySeriesRenderer);


                    final LinearLayout downloadChart = (LinearLayout) findViewById(R.id.chartDownload);
                    XYSeriesRenderer downloadRenderer = new XYSeriesRenderer();
                    XYSeriesRenderer.FillOutsideLine downloadFill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
                    downloadFill.setColor(Color.parseColor("#FFFFFF"));
                    downloadRenderer.addFillOutsideLine(downloadFill);
                    downloadRenderer.setDisplayChartValues(false);
                    downloadRenderer.setColor(Color.parseColor("#FFFFFF"));
                    downloadRenderer.setShowLegendItem(false);
                    downloadRenderer.setLineWidth(5);

                    final XYMultipleSeriesRenderer multiDownloadRenderer = new XYMultipleSeriesRenderer();
                    multiDownloadRenderer.setXLabels(0);
                    multiDownloadRenderer.setYLabels(0);
                    multiDownloadRenderer.setZoomEnabled(false);
                    multiDownloadRenderer.setXAxisColor(Color.parseColor("#647488"));
                    multiDownloadRenderer.setYAxisColor(Color.parseColor("#2F3C4C"));
                    multiDownloadRenderer.setPanEnabled(false, false);
                    multiDownloadRenderer.setZoomButtonsVisible(false);
                    multiDownloadRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                    multiDownloadRenderer.addSeriesRenderer(downloadRenderer);


                    final LinearLayout chartUpload = (LinearLayout) findViewById(R.id.chartUpload);
                    XYSeriesRenderer uploadRenderer = new XYSeriesRenderer();
                    XYSeriesRenderer.FillOutsideLine uploadFill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
                    uploadFill.setColor(Color.parseColor("#FFFFFF"));
                    uploadRenderer.addFillOutsideLine(uploadFill);
                    uploadRenderer.setDisplayChartValues(false);
                    uploadRenderer.setColor(Color.parseColor("#FFFFFF"));
                    uploadRenderer.setShowLegendItem(false);
                    uploadRenderer.setLineWidth(5);

                    final XYMultipleSeriesRenderer multiUploadRenderer = new XYMultipleSeriesRenderer();
                    multiUploadRenderer.setXLabels(0);
                    multiUploadRenderer.setYLabels(0);
                    multiUploadRenderer.setZoomEnabled(false);
                    multiUploadRenderer.setXAxisColor(Color.parseColor("#647488"));
                    multiUploadRenderer.setYAxisColor(Color.parseColor("#2F3C4C"));
                    multiUploadRenderer.setPanEnabled(false, false);
                    multiUploadRenderer.setZoomButtonsVisible(false);
                    multiUploadRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                    multiUploadRenderer.addSeriesRenderer(uploadRenderer);

                    runOnUiThread(() -> {
                        pingTextView.setText("0 ms");
                        chart.removeAllViews();
                        downloadTextView.setText("0 Mbps");
                        downloadChart.removeAllViews();
                        uploadTextView.setText("0 Mbps");
                        chartUpload.removeAllViews();
                    });
                    final List<Double> pingRateList = new ArrayList<>();
                    final List<Double> downloadRateList = new ArrayList<>();
                    final List<Double> uploadRateList = new ArrayList<>();

                    boolean isPingTestStarted = false;
                    boolean pingTestFinished = false;
                    boolean isDownloadTestStarted = false;
                    boolean downloadTestFinished = false;
                    boolean isUploadTestStarted = false;
                    boolean uploadTestFinished = false;

                    //Init Test
                    final PingTester pingTester = new PingTester(info.get(6).replace(":8080", ""), 3);
                    final DownloadSpeedTester downloadTest = new DownloadSpeedTester(testingAddress.replace(testingAddress.split("/")[testingAddress.split("/").length - 1], ""));
                    final HttpUploadTest uploadTest = new HttpUploadTest(testingAddress);

                    try {

                        while (true) {
                            if (!isPingTestStarted) {
                                pingTester.start();
                                isPingTestStarted = true;
                            }
                            if (pingTestFinished && !isDownloadTestStarted) {
                                downloadTest.start();
                                isDownloadTestStarted = true;
                            }
                            if (downloadTestFinished && !isUploadTestStarted) {
                                uploadTest.start();
                                isUploadTestStarted = true;
                            }


                            if (pingTestFinished) {
                                if (pingTester.getAvg() == 0) {
                                    System.out.println("Ping error...");
                                } else {

                                    runOnUiThread(() -> pingTextView.setText(dec.format(pingTester.getAvg()) + " ms"));
                                }
                            } else {
                                pingRateList.add(pingTester.getInstantValue());
                                runOnUiThread(() -> pingTextView.setText(dec.format(pingTester.getInstantValue()) + " ms"));
                                runOnUiThread(() -> {
                                    XYSeries pingSeries = new XYSeries("");
                                    pingSeries.setTitle("");

                                    int count = 0;
                                    List<Double> tmpLs = new ArrayList<>(pingRateList);
                                    for (Double val : tmpLs) {
                                        pingSeries.add(count++, val);
                                    }

                                    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                    dataset.addSeries(pingSeries);

                                    GraphicalView graphicsChart = ChartFactory.getLineChartView(getBaseContext(), dataset, multiPingRenderer);
                                    chart.addView(graphicsChart, 0);

                                });
                            }


                            if (pingTestFinished) {
                                if (downloadTestFinished) {

                                    if (downloadTest.getDownloadingRate() == 0) {
                                        System.out.println("Download error...");
                                    } else {

                                        runOnUiThread(() -> downloadTextView.setText(dec.format(downloadTest.getDownloadingRate()) + " Mbps"));
                                    }
                                } else {

                                    double downloadRate = downloadTest.getDownloadingRateAtStart();
                                    downloadRateList.add(downloadRate);
                                    position = getPosition(downloadRate);

                                    runOnUiThread(() -> {
                                        rotateAnimation = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                        rotateAnimation.setInterpolator(new LinearInterpolator());
                                        rotateAnimation.setDuration(100);
                                        barImageView.startAnimation(rotateAnimation);
                                        downloadTextView.setText(dec.format(downloadTest.getDownloadingRateAtStart()) + " Mbps");

                                    });
                                    lastPosition = position;

                                    runOnUiThread(() -> {
                                        XYSeries downloadSeries = new XYSeries("");
                                        downloadSeries.setTitle("");

                                        List<Double> tmpLs = new ArrayList<>(downloadRateList);
                                        int count = 0;
                                        for (Double val : tmpLs) {
                                            downloadSeries.add(count++, val);
                                        }

                                        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                        dataset.addSeries(downloadSeries);

                                        GraphicalView chartView = ChartFactory.getLineChartView(getBaseContext(), dataset, multiDownloadRenderer);
                                        downloadChart.addView(chartView, 0);
                                    });

                                }
                            }

                            if (downloadTestFinished) {
                                if (uploadTestFinished) {
                                    if (uploadTest.getUploadingRate() == 0) {
                                        System.out.println("Upload error...");
                                    } else {
                                        runOnUiThread(() -> uploadTextView.setText(dec.format(uploadTest.getUploadingRate()) + " Mbps"));
                                    }
                                } else {
                                    double uploadRate = uploadTest.getUploadRate();
                                    uploadRateList.add(uploadRate);
                                    position = getPosition(uploadRate);

                                    runOnUiThread(() -> {
                                        rotateAnimation = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                        rotateAnimation.setInterpolator(new LinearInterpolator());
                                        rotateAnimation.setDuration(100);
                                        barImageView.startAnimation(rotateAnimation);
                                        uploadTextView.setText(dec.format(uploadTest.getUploadRate()) + " Mbps");
                                    });
                                    lastPosition = position;

                                    runOnUiThread(() -> {
                                        XYSeries uploadSeries = new XYSeries("");
                                        uploadSeries.setTitle("");

                                        int count = 0;
                                        List<Double> tmpLs = new ArrayList<>(uploadRateList);
                                        for (Double val : tmpLs) {
                                            if (count == 0) {
                                                val = 0.0;
                                            }
                                            uploadSeries.add(count++, val);
                                        }

                                        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                        dataset.addSeries(uploadSeries);

                                        GraphicalView chartView = ChartFactory.getLineChartView(getBaseContext(), dataset, multiUploadRenderer);
                                        chartUpload.addView(chartView, 0);
                                    });

                                }
                            }

                            if (pingTestFinished && downloadTestFinished && uploadTest.isFinished()) {
                                break;
                            }

                            if (pingTester.isFinished()) {
                                pingTestFinished = true;
                            }
                            if (downloadTest.isFinished()) {
                                downloadTestFinished = true;
                            }
                            if (uploadTest.isFinished()) {
                                uploadTestFinished = true;
                            }

                            if (isPingTestStarted && !pingTestFinished) {
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException ignored) {
                                }
                            } else {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException ignored) {
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(() -> {
                        startBtn.setEnabled(true);
                        startBtn.setTextSize(16);
                        startBtn.setText("Restart Test");
                    });


                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public int getPosition(double rate) {
        if (rate <= 1) {
            return (int) (rate * 30);

        } else if (rate <= 10) {
            return (int) (rate * 6) + 30;

        } else if (rate <= 30) {
            return (int) ((rate - 10) * 3) + 90;

        } else if (rate <= 50) {
            return (int) ((rate - 30) * 1.5) + 150;

        } else if (rate <= 100) {
            return (int) ((rate - 50) * 1.2) + 180;
        }

        return 0;
    }
}

