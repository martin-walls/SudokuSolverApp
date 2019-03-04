package com.example.sudokusolver;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Board {
    static int BOARDSIZE = 9;
    static int BLANK = 0;
    private int[] DIGITS = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    private int[][] board = new int[9][9];

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

    public int[][] getBoard() {
        return board;
    }

    public void resetBoard() {
        for (int row = 0; row < BOARDSIZE; row++) {
            for (int col = 0; col < BOARDSIZE; col++) {
                board[row][col] = BLANK;
            }
        }
    }

    public void setSquare(int row, int col, int val) {
        board[row][col] = val;
    }

    public int getSquare(int row, int col) {
        return board[row][col];
    }

    public int[] getRow(int row) {
        return board[row];
    }

    public int[] getCol(int col) {
        int[] result = new int[9];
        for (int i = 0; i < BOARDSIZE; i++) {
            result[i] = board[i][col];
        }
        return result;
    }

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
