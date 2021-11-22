package com.jumbo.trus.web;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {
    private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback<R> {
        void onComplete(R result);
    }

    public <R> void executeAsync(final Callable<R> callable, final Callback<R> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                R result = null;
                try {
                    result = callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final R finalResult = result;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onComplete(finalResult);
                    }
                });
            }
        });
    }
}
