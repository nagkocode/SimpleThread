
// Run a runnable thread.

package com.example.snippet.simplethread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // control
    private TextView textView;
    private Button button1, button2;

    private Counter counter;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // reference control
        textView = findViewById(R.id.textView);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        // this is a means of communication between working thread and main thread
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {

                // Extract the count from the Message
                long count = (long) msg.obj;

                // display the count
                String str = String.format("%3d", count);
                textView.setText(str);
            }
        };

        // enable start button, disable stop button
        button1.setEnabled(true);
        button2.setEnabled(false);

        // start button click event
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                // run thread
                counter = new Counter();
                new Thread(counter).start();

                // enable stop button, disable start button
                button1.setEnabled(false);
                button2.setEnabled(true);
            }
        });

        // stop button click event
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                // stop thread
                counter.stop();

                // enable start button, disable stop button
                button1.setEnabled(true);
                button2.setEnabled(false);
            }
        });
    }

    // implement runnable class
    public class Counter implements Runnable{

        private Thread thread;

        @Override
        public void run() {

            boolean loop;
            String str;
            Message msg;
            long count;

            // use background priority
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            // store current thread in local variable
            thread =  Thread.currentThread();

            // output to Logcat window
            str = String.format("%s HAS STARTED.", thread);
            Log.d("LOGCAT", str);

            // loop
            loop = true;
            count = 0;

            while(loop){

                // delay
                SystemClock.sleep(100);

                // increment
                ++count;

                // send count to handler
                msg = Message.obtain();
                msg.obj = count;
                msg.setTarget(handler);
                msg.sendToTarget();

                // break the loop
                if(thread.isInterrupted() || count == Long.MAX_VALUE)
                    loop = false;
            }

            // output to Logcat window
            str = String.format("%s HAS STOPED.", thread);
            Log.d("LOGCAT", str);
        }

        // stop thread
        public void stop(){
            if(thread != null)
                thread.interrupt();
        }
    }
}
