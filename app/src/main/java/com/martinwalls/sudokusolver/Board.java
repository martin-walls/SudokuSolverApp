package com.martinwalls.sudokusolver;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    private static final int BOARDSIZE = 9;
    static final int BLANK = 0;
    private final int[] DIGITS = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    // initialise blank board
    private int[][] board = new int[9][9];

    /**
     * Gets a List of all the blank squares in the grid at the start, i.e. the squares
     * entered by the user.
     * @return a List of the original squares
     */
    public List<int[]> getOriginalSquares() {
        List<int[]> originalSquares = new ArrayList<>();
        for (int row = 0; row < BOARDSIZE; row++) {
            for (int col = 0; col < BOARDSIZE; col++) {
                if (board[row][col] != BLANK) {
                    originalSquares.add(new int[] {row, col});
                }
            }
        }
        return originalSquares;
    }

    /**
     * Sets the value of the square at {@code [row, col]} to {@code val}
     */
    public void setSquare(int row, int col, int val) {
        board[row][col] = val;
    }

    /**
     * Returns the value of the square at {@code [row, col]}
     */
    public int getSquare(int row, int col) {
        return board[row][col];
    }

    /**
     * Returns an array of the values in the specified row
     */
    public int[] getRow(int row) {
        return board[row];
    }

    /**
     * Returns an array of the values in the specified column
     */
    public int[] getCol(int col) {
        int[] result = new int[9];
        for (int i = 0; i < BOARDSIZE; i++) {
            result[i] = board[i][col];
        }
        return result;
    }

    /**
     * Returns an array of the values in the box surrounding the square at {@code [row, col]}
     */
    public int[] getBox(int row, int col) {
        int[][] groups = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};

        int[] rowGroup = groups[row / 3];
        int[] colGroup = groups[col / 3];

        int[] valsInBox = new int[9];
        int i = 0;
        for (int r : rowGroup) {
            for (int c : colGroup) {
                valsInBox[i] = board[r][c];
                i++;
            }
        }
        return valsInBox;
    }

    /**
     * Returns a List of all the blank squares currently on the board
     */
    public List<int[]> getBlankSquares() {
        List<int[]> blankSquares = new ArrayList<>();
        for (int row = 0; row < BOARDSIZE; row++) {
            for (int col = 0; col < BOARDSIZE; col++) {
                if (board[row][col] == BLANK) {
                    blankSquares.add(new int[] {row, col});
                }
            }
        }
        return blankSquares;
    }

    public boolean checkFinished() {
        Log.d("DEBUG", "checkFinished");
        for (int row = 0; row < BOARDSIZE; row++) {
            for (int col = 0; col < BOARDSIZE; col++) {
                if (board[row][col] == BLANK) {
                    return false;
                }
            }
        }
//        return verifySolved();
        return true;
    }

    private boolean verifySolved() {
        for (int row = 0; row < BOARDSIZE; row++) {
            for (int col = 0; col < BOARDSIZE; col++) {
                if (!verifyRowSolved(row) || !verifyColSolved(col) || !verifyBoxSolved(row, col)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean verifyRowSolved(int row) {
        int[] rowToCheck = getRow(row);
        Arrays.sort(rowToCheck);
        return rowToCheck == DIGITS;
    }

    private boolean verifyColSolved(int col) {
        int[] colToCheck = getCol(col);
        Arrays.sort(colToCheck);
        return colToCheck == DIGITS;
    }

    private boolean verifyBoxSolved(int row, int col) {
        int[] boxToCheck = getBox(row, col);
        Arrays.sort(boxToCheck);
        return boxToCheck == DIGITS;
    }
}
