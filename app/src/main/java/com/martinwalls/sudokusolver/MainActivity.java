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

    // the layout holding all the sudoku squares
    private GridLayout sudokuGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sudokuGrid = findViewById(R.id.sudoku_grid);

        GridLayout keypadGrid = findViewById(R.id.keypad);

        // set on click listeners for clear, reset and solve buttons
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

    /**
     * OnClick function for the numbers 1-9 on the keypad, called from {@code onClick} attribute in {@code styles.xml}
     * @param view the calling view (
     */
    public void keypadNumberOnClick(View view) {
        // get the sudoku square that is currently focused, if it exists
        TextView focusedView = (TextView) getCurrentFocus();
        if (focusedView != null) {
            focusedView.setText(((Button) view).getText().toString());
        }
    }

    /**
     * Set the focused square blank, if a square is focused.
     */
    public void clearFocusedSquare() {
        TextView focusedView = (TextView) getCurrentFocus();
        if (focusedView != null) {
            focusedView.setText(getString(R.string.blank_square));
        }
    }

    /**
     * Colour the squares the user has entered, to distinguish them from the output of the program
     * @param originalSquares the squares filled in by the user
     */
    private void setOriginalSquares(List<int[]> originalSquares) {
        for (int i = 0; i < sudokuGrid.getChildCount(); i++) {
            // get box of the grid
            GridLayout box = (GridLayout) sudokuGrid.getChildAt(i);
            for (int j = 0; j < box.getChildCount(); j++) {
                // calculate row and column from box and row within box
                int row = (i/3) * 3 + j/3;
                int col = (i%3) * 3 + j%3;
                for (int[] originalSquare : originalSquares) {
                    // if the square is an original square
                    if (row == originalSquare[0] && col == originalSquare[1]) {
                        TextView square = (TextView) box.getChildAt(j);
                        square.setBackground(getDrawable(R.drawable.sudoku_cell_original));
                    }
                }
            }
        }
    }

    /**
     * Remove focus from the current focused square, if it exists
     */
    private void clearFocus() {
        View focused = getCurrentFocus();
        if (focused != null) {
            focused.clearFocus();
        }
    }

    /**
     * Reset the board to a blank grid
     */
    private void resetBoard() {
        for (int i = 0; i < sudokuGrid.getChildCount(); i++) {
            GridLayout box = (GridLayout) sudokuGrid.getChildAt(i);
            for (int j = 0; j < box.getChildCount(); j++) {
                TextView square = (TextView) box.getChildAt(j);
                square.setText(getString(R.string.blank_square));
                square.setBackground(getDrawable(R.drawable.sudoku_cell));
                // allow the user to interact with the grid again
                square.setFocusableInTouchMode(true);
            }
        }
        // remove focus from the focused view, so no square is highlighted
        clearFocus();
    }

    /**
     * Update the grid to the output of the solving algorithm
     * @param board solution to the given Sudoku
     */
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
                // prevent the user interacting with the board in its solved state
                square.setFocusableInTouchMode(false);
            }
        }
        clearFocus();
    }

    /**
     * Private inner class for the solving algorithm to run in a background thread to make sure
     * it doesn't interrupt the UI thread if it takes a long time.
     */
    private class Solver extends Thread {
        private Board board = new Board();
        private List<int[]> squaresToSolve;
        private List<int[]> originalSquares;
        private int numMoves;

        @Override
        public void run() {
            // retrieve the board from the grid, and check
            // if the user has entered a valid state of the board
            boolean boardIsValid = getBoard();
            // get a list of all the blank squares and original squares entered by the user
            squaresToSolve = board.getBlankSquares();
            originalSquares = board.getOriginalSquares();
            // if board is solvable, and is not full or empty
            if (boardIsValid && squaresToSolve.size() != 0 && originalSquares.size() != 0) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setOriginalSquares(originalSquares);
                    }
                });

                // solve grid
                int pointer = 0;
                iterValsFor(pointer);

                // output solution
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateGridToSolution(board);
                        Toast.makeText(MainActivity.this, getString(R.string.solved_num_moves, numMoves), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (squaresToSolve.size() == 0) { // if the board is full (no blank squares)
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, getString(R.string.solveError_gridFull), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (originalSquares.size() == 0) { // if the board is empty (no squares entered)
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, getString(R.string.solveError_gridEmpty), Toast.LENGTH_SHORT).show();
                    }
                });
            } else { // if the board is not full/empty, but not in a valid state
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, getString(R.string.solveError_unsolvable), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        /**
         * Retrieve the board from the GridLayout, and check if it is valid by entering each
         * square one at a time to check if it is valid.
         * @return {@code true} if the board is valid. {@code false} if not.
         */
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
                            // digit not valid, so board not valid
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

        /**
         * Check if a digit is valid in that position
         * @param val digit to check
         * @param row row of grid
         * @param col col of grid
         * @return validity of digit
         */
        private boolean isDigitValid(int val, int row, int col) {
            return !isValInRow(val, row) && !isValInCol(val, col) && !isValInBox(val, row, col);
        }

        /**
         * Set the square at {@code pointer} to val
         * @param pointer index of square in {@code squaresToSolve}
         * @param val the digit to change the square to
         */
        private void updateSquare(int pointer, int val) {
            board.setSquare(squaresToSolve.get(pointer)[0], squaresToSolve.get(pointer)[1], val);
//            updateEditText(String.valueOf(val), squaresToSolve.get(pointer)[0], squaresToSolve.get(pointer)[1]);
            numMoves++;
        }

        /**
         * Iterate through the digits 1-9 for the square at {@code pointer}. For each, checks
         * if digit is valid, if it is, moves to the next square in {@code squaresToSolve} and
         * and calls this function recursively. If the digit is invalid it skips that digit.
         * Stops when it reaches the end of {@code squaresToSolve}.
         * @param pointer index of current square in {@code squaresToSolve}
         * @return whether the current state is valid or not
         */
        private boolean iterValsFor(int pointer) {
            for (int digit = 1; digit < 10; digit++) {
                if (isDigitValid(digit, squaresToSolve.get(pointer)[0], squaresToSolve.get(pointer)[1])) {
                    updateSquare(pointer, digit);

                    // stop when it reaches the end of the grid
                    if (pointer == squaresToSolve.size()-1) {
                        return true;
                    }

                    if (pointer < squaresToSolve.size()-1) {
                        boolean solved = iterValsFor(pointer + 1);
                        if (solved) {
                            return true;
                        }
                    }

                    updateSquare(pointer, Board.BLANK);
                }
            }
            // if none of the digits were valid, the square before must be invalid
            return false;
        }
    }
}
