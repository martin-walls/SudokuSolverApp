package com.example.sudokusolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

//    private ProgressBar progressBar;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout gridLayout = findViewById(R.id.grid_layout);

        // initialise 9x9 grid of EditText views
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                final EditText editText = new EditText(this);
//                editText.setText(String.valueOf(col + col/3));
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (editText.getText().toString().equals("0")) {
                            editText.setText("");
                            return;
                        }
                        if (editText.getText().toString().length() == 1) {
                            EditText nextView = (EditText) editText.focusSearch(View.FOCUS_FORWARD);
                            nextView.requestFocus();
//                            editText.requestFocus(View.FOCUS_FORWARD);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.width = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(col);
                params.rowSpec = GridLayout.spec(row);
                editText.setLayoutParams(params);

                gridLayout.addView(editText);
            }
        }

        Button solveBtn = findViewById(R.id.btn_solve);
        solveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                new Solver().start();
            }
        });

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

//    private void showProgressDialog() {
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setMessage("Solving...");
//        progressDialog.setCancelable(false);
//        progressDialog.setMax(100);
//        progressDialog.show();
//    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog();

        progressDialog.show(getSupportFragmentManager(), "progress");
    }

    private class Solver extends Thread {

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    progressDialog.incrementProgressBy(1);
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
