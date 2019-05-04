package com.martinwalls.sudokusolver;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.GridLayout;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

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

        /*
        Button solveBtn = findViewById(R.id.btn_solve);
        solveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                new Solver().start();
            }
        });

        Button resetBtn = findViewById(R.id.btn_reset);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBoard();
            }
        });*/
    }

//    public void squareOnClick(View view) {
//        view.requestFocus();
//    }

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
//    }

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

    private void setOriginalSquaresBold(List<int[]> squares) {
        for (int[] square : squares) {
            EditText editText = gridLayout.findViewById(
                    getResources().getIdentifier("s" + square[0] + square[1],
                            "id", getPackageName()));
            editText.setTypeface(editText.getTypeface(), Typeface.BOLD);
        }
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

    private void resetBoard() {
        for (int row = 0; row < Board.BOARDSIZE; row++) {
            for (int col = 0; col < Board.BOARDSIZE; col++) {
                EditText editText = gridLayout.findViewById(
                        getResources().getIdentifier("s" + row + col, "id", getPackageName()));
                editText.setTypeface(editText.getTypeface(), Typeface.NORMAL); //FIXME
                editText.setText("");
            }
        }
    }

    private class Solver extends Thread {
        private Board board = new Board();
        private List<int[]> squaresToSolve;
        private List<int[]> originalSquares;
        private int numMoves = 0;

        @Override
        public void run() {
            getBoard();
            squaresToSolve = board.getBlankSquares();
            originalSquares = board.getOriginalSquares();
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setOriginalSquaresBold(originalSquares);

                }
            });

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
        }

        private boolean iterValsFor(int pointer) {
            boolean solved = false;
            for (int digit = 1; digit < 10; digit++) {
                if (isDigitValid(digit, squaresToSolve.get(pointer)[0], squaresToSolve.get(pointer)[1])) {
                    updateSquare(pointer, digit);

                    if (pointer == squaresToSolve.size()-1) {
                        return true;
                    }

                    if (pointer < squaresToSolve.size()-1) {
                        solved = iterValsFor(pointer + 1);
                        if (solved) {
                            return true;
                        }
                    }

                    updateSquare(pointer, Board.BLANK);
                }
            }
            return solved;
        }
    }
}
