package com.example.sudokusolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private GridLayout gridLayout;
    private Board boardDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.grid_layout);

        // initialise 9x9 grid of EditText views
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                final EditText editText = new EditText(this);
//                editText.setText(String.valueOf(col + col/3));
                editText.setId(getResources().getIdentifier("s"+row+col, "id", getPackageName()));
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

    private void updateGrid(Board board) {
        for (int row = 0; row < Board.BOARDSIZE; row++) {
            for (int col = 0; col < Board.BOARDSIZE; col++) {
                EditText editText = gridLayout.findViewById(
                        getResources().getIdentifier("s"+row+col, "id", getPackageName()));
                int squareVal = board.getSquare(row, col);
                if (squareVal == 0) {
                    editText.setText("");
                } else {
                    editText.setText(String.valueOf(squareVal));
                }
            }
        }
    }

    private class Solver extends Thread {
        private Board board = new Board();
        private List<int[]> squaresToSolve;
        private int numMoves = 0;
        private boolean solved = false;

        @Override
        public void run() {
            getBoard();
            squaresToSolve = board.getBlankSquares();
            Log.d("DEBUG", "run");

            int pointer = 0;
            iterValsFor(pointer);

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateGrid(board);
                    progressDialog.dismiss();
                }
            });

        }

        private void getBoard() {
            for (int row = 0; row < Board.BOARDSIZE; row++) {
                for (int col = 0; col < Board.BOARDSIZE; col++) {
                    EditText editText = gridLayout.findViewById(
                            getResources().getIdentifier("s"+row+col, "id", getPackageName()));
                    String valStr = editText.getText().toString();
                    int val;
                    if (valStr.equals("")) {
                        val = Board.BLANK;
                    } else {
                        val = Integer.parseInt(valStr);
                    }
                    board.setSquare(row, col, val);
                }
            }
        }

        private boolean isValInRow(int val, int row) {
            for (int v : board.getRow(row)) {
                if (v == val) {
                    return true;
                }
            }
            return false;
        }

        private boolean isValInCol(int val, int col) {
            for (int v : board.getCol(col)) {
                if (v == val) {
                    return true;
                }
            }
            return false;
        }

        private boolean isValInBox(int val, int row, int col) {
            for (int v : board.getBox(row, col)) {
                if (v == val) {
                    return true;
                }
            }
            return false;
        }

        private boolean isDigitValid(int val, int row, int col) {
            return !isValInRow(val, row) && !isValInCol(val, col) && !isValInBox(val, row, col);
        }

        private void updateSquare(int pointer, int val) {
            board.setSquare(squaresToSolve.get(pointer)[0], squaresToSolve.get(pointer)[1], val);
//            updateEditText(String.valueOf(val), squaresToSolve.get(pointer)[0], squaresToSolve.get(pointer)[1]);
            numMoves++;
            Log.d("DEBUG", Arrays.deepToString(board.getBoard()));
        }

        private boolean iterValsFor(int pointer) {
            boolean solved = false;
            for (int digit = 1; digit < 10; digit++) {
//                    Log.d("DEBUG", "next digit: " + digit);
                if (isDigitValid(digit, squaresToSolve.get(pointer)[0], squaresToSolve.get(pointer)[1])) {
                    updateSquare(pointer, digit);

                    if (pointer == squaresToSolve.size()-1) {
                        finished();
                        return true;
                    }

                    if (pointer < squaresToSolve.size()-1) {
                        solved = iterValsFor(pointer + 1);
                        if (solved) {
                            return true;
                        }
                    }

                    if (!solved) {
                        updateSquare(pointer, Board.BLANK);
//                            Log.d("DEBUG", "reset square");
                    }
                }
            }
            return solved;
        }

        private void finished() {
            Log.e("DEBUG", Arrays.deepToString(board.getBoard()));
        }
    }
}
