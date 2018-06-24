package com.aditep.labthread;

import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aditep.labthread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    int counter;
    Thread thread;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        counter = 0;

        //Thread Method 1: Thread
        /*
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Run in background
                for (int i = 0; i < 100; i++) {
                    counter++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // UI Thread a.k.a Main Thread
                            binding.tvCounter.setText(counter + "");
                        }
                    });
                }

            }
        });
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        thread.interrupt();
    }
    */
        //Thread Method 2: Thread with Handler
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // Run in Main Thread
                binding.tvCounter.setText(msg.arg1 + "");
            }
        };
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Run in background
                for (int i = 0; i < 100; i++) {
                    counter++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }


                    Message msg = new Message();
                    msg.arg1 = counter;
                    handler.sendMessage(msg);

                }

            }
        });
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        thread.interrupt();
    }
}
