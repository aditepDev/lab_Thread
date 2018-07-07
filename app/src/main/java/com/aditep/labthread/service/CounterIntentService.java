package com.aditep.labthread.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class CounterIntentService extends IntentService {
    public CounterIntentService() {
        super("");
    }

    public CounterIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Run in Background Thread
        for (int i = 0; i < 100; i++) {
            Log.d("IntentService", "i=" + i);
            Intent broadcastIntent = new Intent("CounterIntentServiceUpdate");
            broadcastIntent.putExtra("counter",i);
            LocalBroadcastManager.getInstance(CounterIntentService.this)
                    .sendBroadcast(broadcastIntent);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
