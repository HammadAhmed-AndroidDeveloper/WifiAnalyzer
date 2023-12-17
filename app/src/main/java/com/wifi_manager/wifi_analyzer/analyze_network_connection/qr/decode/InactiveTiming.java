package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.decode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class InactiveTiming {

    private static final int SECONDS = 5 * 60;

    private final ScheduledExecutorService _service = Executors.newSingleThreadScheduledExecutor(new MyThreadFactory());
    private final AppCompatActivity activity;
    private ScheduledFuture<?> sf = null;

    public InactiveTiming(AppCompatActivity activity) {
        this.activity = activity;
        onActivity();
    }

    public void onActivity() {
        cancel();
        sf = _service.schedule(new OnCompletedListener(activity), SECONDS, TimeUnit.SECONDS);
    }

    private void cancel() {
        if (sf != null) {
            sf.cancel(true);
            sf = null;
        }
    }

    public void shutdown() {
        cancel();
        _service.shutdown();
    }

    private static final class MyThreadFactory implements ThreadFactory {
        public Thread newThread(@NonNull Runnable runnable) {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        }
    }

}
