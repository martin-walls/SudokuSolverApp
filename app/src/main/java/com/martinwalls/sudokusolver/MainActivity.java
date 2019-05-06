package com.martinwalls.sudokusolver;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

//    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private GridLayout sudokuGrid;

    private TextView focusedSquare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sudokuGrid = findViewById(R.id.sudoku_grid);

        GridLayout keypadGrid = findViewById(R.id.keypad);

        Button clearButton = keypadGrid.findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFocusedSquare();
            }
        });

        Button resetButton = keypadGrid.findViewById(R.id.button_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBoard();
            }
        });

        Button solveButton = keypadGrid.findViewById(R.id.button_solve);
        solveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Solver().start();
            }
        });

    }

    public void keypadNumberOnClick(View view) {
        TextView focusedView = (TextView) getCurrentFocus();
        if (focusedView != null) {
            focusedView.setText(((Button) view).getText().toString());
        }
    }

    public void clearFocusedSquare() {
        TextView focusedView = (TextView) getCurrentFocus();
        if (focusedView != null) {
            focusedView.setText(getString(R.string.blank_square));
        }
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog();
        progressDialog.show(getSupportFragmentManager(), "progress");
    }

    //TODO
    private void setOriginalSquares(List<int[]> originalSquares) {
        for (int i = 0; i < sudokuGrid.getChildCount(); i++) {
            GridLayout box = (GridLayout) sudokuGrid.getChildAt(i);
            for (int j = 0; j < box.getChildCount(); j++) {
                int row = (i/3) * 3 + j/3;
                int col = (i%3) * 3 + j%3;
                for (int[] originalSquare : originalSquares) {
                    if (row == originalSquare[0] && col == originalSquare[1]) {
                        TextView square = (TextView) box.getChildAt(j);
                        square.setBackground(getDrawable(R.drawable.sudoku_cell_original));
                    }
                }
            }
        }
    }

    private void clearFocus() {
        View focused = getCurrentFocus();
        if (focused != null) {
            focused.clearFocus();
        }
    }

    private void resetBoard() {
        for (int i = 0; i < sudokuGrid.getChildCount(); i++) {
            GridLayout box = (GridLayout) sudokuGrid.getChildAt(i);
            for (int j = 0; j < box.getChildCount(); j++) {
                TextView square = (TextView) box.getChildAt(j);
                square.setText(getString(R.string.blank_square));
                square.setBackground(getDrawable(R.drawable.sudoku_cell));
                square.setFocusableInTouchMode(true);
            }
        }
        clearFocus();
    }

    private void updateGridToSolution(Board board) {
        for (int i = 0; i < sudokuGrid.getChildCount(); i++) {
            GridLayout box = (GridLayout) sudokuGrid.getChildAt(i);
            for (int j = 0; j < box.getChildCount(); j++) {
                TextView square = (TextView) box.getChildAt(j);
                int row = (i/3) * 3 + j/3;
                int col = (i%3) * 3 + j%3;

                int val = board.getSquare(row, col);
                if (val == Board.BLANK) {
                    square.setText(getString(R.string.blank_square));
                } else {
                    square.setText(String.valueOf(val));
                }
                square.setFocusableInTouchMode(false);
            }
        }
        clearFocus();
    }

    private class Solver extends Thread {
        private Board board = new Board();
        private List<int[]> squaresToSolve;
        private List<int[]> originalSquares;
        private int numMoves = 0;

        @Override
        public void run() {
            if (getBoard()) {
                squaresToSolve = board.getBlankSquares();
                originalSquares = board.getOriginalSquares();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setOriginalSquares(originalSquares);
                    }
                });

                int pointer = 0;
                iterValsFor(pointer);

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateGridToSolution(board);
//                        progressDialog.dismiss();
                    }
                });
            } else {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Error: unsolvable grid", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        private boolean getBoard() {
            for (int i = 0; i < sudokuGrid.getChildCount(); i++) {
                GridLayout box = (GridLayout) sudokuGrid.getChildAt(i);
                for (int j = 0; j < box.getChildCount(); j++) {
                    int row = (i/3) * 3 + j/3;
                    int col = (i%3) * 3 + j%3;
                    TextView square = (TextView) box.getChildAt(j);
                    String valString = square.getText().toString();
                    int val;
                    if (valString.equals(getString(R.string.blank_square))) {
                        val = Board.BLANK;
                        board.setSquare(row, col, val);
                    } else {
                        val = Integer.parseInt(valString);
                        if (isDigitValid(val, row, col)) {
                            board.setSquare(row, col, val);
                        } else {
                            return false;
                        }
                    }
                }
            }
            return true;
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
