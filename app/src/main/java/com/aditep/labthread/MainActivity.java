package com.aditep.labthread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.aditep.labthread.databinding.ActivityMainBinding;
import com.aditep.labthread.service.CounterIntentService;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {
    ActivityMainBinding binding;

    int counter;
    Thread thread;
    Handler handler;

    HandlerThread backgroudHandlerThread;
    Handler backgroundHandler;
    Handler mainHandler;

    SampleAsyncTask sampleAsyncTask;

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
        /*
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
         */
        // Thread Method 3:Handler Only
        /*
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                counter++;
                binding.tvCounter.setText(counter + "");
                if (counter < 100)
                    sendEmptyMessageDelayed(0 , 1000);
            }
        };
        handler.sendEmptyMessageDelayed(0,1000)
        */
        // Thread Method 4: HandlerThread
        /*
        backgroudHandlerThread = new HandlerThread("BackgroundHandlerThread");
        backgroudHandlerThread.start();

        backgroundHandler = new Handler(backgroudHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //run with background
                Message msgMain = new Message();
                msgMain.arg1 = msg.arg1 + 1;
                mainHandler.sendMessage(msgMain);
            }
        };
        mainHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //Run with Main Thread
                binding.tvCounter.setText(msg.arg1 + "");
                if (msg.arg1 < 100){
                    Message msgBack = new Message();
                    msgBack.arg1 = msg.arg1;
                    backgroundHandler.sendMessageDelayed(msgBack,1000);
                }
            }
        };
        Message msgBack = new Message();
        msgBack.arg1 = 0;
        backgroundHandler.sendMessageDelayed(msgBack,1000);
    */

        //Thread Method 5: AsyncTask
        //  sampleAsyncTask = new SampleAsyncTask();
        // sampleAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0,100);

        //Thread Method 6: AsyncTaskLoader
        //getSupportLoaderManager().initLoader(1,null, this);
         // Thread Method 7: IntentService
        LocalBroadcastManager.getInstance(MainActivity.this)
                .registerReceiver(counterBoroadcastReceiver, new IntentFilter("CounterIntentServiceUpdate"));

        Intent intent = new Intent(MainActivity.this, CounterIntentService.class);
        intent.putExtra("abc","123");
        startService(intent);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(MainActivity.this)
        .unregisterReceiver(counterBoroadcastReceiver);

        // thread.interrupt();
        //  backgroudHandlerThread.quit();
        //  sampleAsyncTask.cancel(true);
    }


    protected BroadcastReceiver counterBoroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int counter = intent.getIntExtra("counter",0);
            binding.tvCounter.setText(counter + "");
        }
    };
    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == 1) {
            return  new AdderAsyncTaskLoader(MainActivity.this, 5 , 11);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object o) {
        Log.d("LLLL","onSLoadFinished");
        if (loader.getId() == 1) {
            Integer result = (Integer) o;
            binding.tvCounter.setText(result + "");

        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

   static class AdderAsyncTaskLoader extends AsyncTaskLoader<Object> {

        int a, b;
       Integer result;
       Handler handler;
        public AdderAsyncTaskLoader(@NonNull Context context, int a, int b) {
            super(context);
            this.a = a;
            this.b = b;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            Log.d("LLLL","onStartLoading");
            if (result != null){
                deliverResult(result);
            }
            // Initialize Handler
            if (handler == null)
                handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        a = (int)(Math.random() * 100);
                        b = (int)(Math.random() * 100);
                        onContentChanged();
                        handler.sendEmptyMessageDelayed(0,3000);
                    }
                };
            handler.sendEmptyMessageDelayed(0,3000);
            if (takeContentChanged() || result == null ) {
                forceLoad();
            }
        }

        @Nullable
        @Override
        public Integer loadInBackground() {
            Log.d("LLLL","LoadinBackground");
            // Background Thread
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            result =  a + b;
            return  result;
        }

       @Override
       protected void onStopLoading() {
           super.onStopLoading();
           Log.d("LLLL","onStopLoading");
       }

       @Override
       protected void onReset() {
           super.onReset();
           // Destroy handler
           if (handler != null){
               handler.removeCallbacksAndMessages(null);
               handler = null;
           }
       }
   }

    class SampleAsyncTask extends AsyncTask<Integer, Float, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... integers) {
            // Run in Background Thread
            int start = integers[0]; //0
            int end = integers[1];   //1
            for (int i = start; i < end; i++) {
                try {
                    thread.sleep(1000);
                } catch (InterruptedException e) {
                    return false;
                }
                publishProgress(i + 0.0f);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            //Run on Main Thread
            super.onProgressUpdate(values);
            float progress = values[0];
            binding.tvCounter.setText(progress + "%");
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            // Run on Main Thread
            super.onPostExecute(aBoolean);

        }
    }
}
