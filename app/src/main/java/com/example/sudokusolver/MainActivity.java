package com.example.sudokusolver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setMessage("Progress dialog");
//        progressDialog.setCancelable(false);
//        progressDialog.setMax(100);
//        progressDialog.show();


//        progressBar = findViewById(R.id.progress_bar);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (progressBar.getProgress() < 100) {
//                    progressBar.incrementProgressBy(1);
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();

//        new Solver().start();


    }

    private class Solver extends Thread {

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    progressBar.incrementProgressBy(1);
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
