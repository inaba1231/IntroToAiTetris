package Tetris;

import java.util.Random;

public class Player {

    public static final int COLS = 10;
    public static final int ROWS = 21;

    //implement this function to have a working system
    public int[] pickMove(State s, int[][] legalMoves) {
        double max = -Double.MAX_VALUE;
        int[] move = {0, 0};
        for (int[] x : legalMoves) {
            int[][] field = copy(s.getField());
            makeMove(field, x[0], x[1], s);
            double value = heuristic(field);
            //System.out.println(value);
            if (value > max) {
                max = value;
                move[0] = x[0];
                move[1] = x[1];
            }
        }
        return move;
    }

    final static int size = 21;

    //Lucky guess
    static double[] weight = {-2, -4, -6, -8, -10, -10, -8, -6, -4, -2, 8, 6, 4, 2, 2, -2, -4, -6, -8, -50, -40};
    //Zeroes
    //static double[] weight = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    //Temporary trained
    //static double[] weight = {1.0, 1.0, 2.0, -5.0, -10.0, -8.0, -2.0, -9.0, 2.0, -3.0, 0.0, -1.0, -7.0, -6.0, -5.0, -3.0, -6.0, 4.0, -2.0, -7.0, -13.0};

    public double heuristic(int[][] field) {
        if (field[0][0] == -1) {
            return -Double.MAX_VALUE;
        }
        double[] feature = new double[size];

        //0-9   height
        for (int i = 0; i < 10; i++) {
            feature[i] = colHeight(field, i);
        }

        //10-18 diff btw col heights
        for (int i = 0; i < 9; i++) {
            feature[i + 10] = feature[i] - feature[i + 1];
        }

        //19    max col height
        double max = 0;
        for (int i = 0; i < 10; i++) {
            if (feature[i] > max) {
                max = feature[i];
            }
        }
        feature[19] = max;

        //20    num holes
        int holes = 0;
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS - 2; j++) {
                if (field[j][i] == 0 && j < feature[i]) {
                    holes++;
                }
            }
        }
        feature[20] = holes;

        double value = weight[0];
        for (int i = 0; i < size; i++) {
            value += weight[i] * feature[i];
        }
        return value;
    }

    public int colHeight(int[][] field, int col) {
        for (int x = ROWS - 2; x >= 0; x--) {
            if (field[x][col] != 0) {
                return x + 1;
            }
        }
        return 0;
    }

    public int[][] copy(int[][] a) {
        int[][] field = new int[ROWS][COLS];
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                field[j][i] = a[j][i];
            }
        }
        return field;
    }

    public void makeMove(int[][] field, int orient, int slot, State s) {
        int nextPiece = s.getNextPiece();
        int[] top = new int[COLS];
        System.arraycopy(s.getTop(), 0, top, 0, COLS);

        //height if the first column makes contact
        int height = top[slot] - pBottom[nextPiece][orient][0];
        //for each column beyond the first in the piece
        for (int c = 1; c < pWidth[nextPiece][orient]; c++) {
            height = Math.max(height, top[slot + c] - pBottom[nextPiece][orient][c]);
        }

        //check if game ended
        if (height + pHeight[nextPiece][orient] >= ROWS) {
            field[0][0] = -1;
            return;
        }

        //for each column in the piece - fill in the appropriate blocks
        for (int i = 0; i < pWidth[nextPiece][orient]; i++) {

            //from bottom to top of brick
            for (int h = height + pBottom[nextPiece][orient][i]; h < height + pTop[nextPiece][orient][i]; h++) {
                field[h][i + slot] = 1;
            }
        }

        //check for full rows - starting at the top
        for (int r = height + pHeight[nextPiece][orient] - 1; r >= height; r--) {
            //check all columns in the row
            boolean full = true;
            for (int c = 0; c < COLS; c++) {
                if (field[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            //if the row was full - remove it and slide above stuff down
            if (full) {
                //for each column
                for (int c = 0; c < COLS; c++) {

                    //slide down all bricks
                    for (int i = r; i < top[c]; i++) {
                        field[i][c] = field[i + 1][c];
                    }
                    //lower the top
                    top[c]--;
                    while (top[c] >= 1 && field[top[c] - 1][c] == 0) {
                        top[c]--;
                    }
                }
            }
        }
    }
/*
    public static void main(String[] args) {
        int iter = 10000;
        int step = 1;
        for (int a = 0; a < iter; a++) {
            iteration();
        }
    }

    public static int iteration() {
        int loop = 10;
        int sum = 0;
        for (int i = 0; i < loop; i++) {
            State s = new State();
            PlayerSkeleton p = new PlayerSkeleton();
            while (!s.hasLost()) {
                s.makeMove(p.pickMove(s, s.legalMoves()));
            }
            //System.out.print(s.getRowsCleared() + " ");
            sum += s.getRowsCleared();
        }
        //System.out.println();
        //System.out.println(sum / loop);
        return sum / loop;
    }
    */

    public void p(int[][] a) {
        for (int[] x : a) {
            for (int y : x) {
                System.out.print(y + " ");
            }
            System.out.println("");
        }
        System.out.println("----");
    }

    //possible orientations for a given piece type
    protected static int[] pOrients = {1, 2, 4, 4, 4, 2, 2};

    //the next several arrays define the piece vocabulary in detail
    //width of the pieces [piece ID][orientation]
    protected static int[][] pWidth = {
        {2},
        {1, 4},
        {2, 3, 2, 3},
        {2, 3, 2, 3},
        {2, 3, 2, 3},
        {3, 2},
        {3, 2}
    };
    //height of the pieces [piece ID][orientation]
    private static int[][] pHeight = {
        {2},
        {4, 1},
        {3, 2, 3, 2},
        {3, 2, 3, 2},
        {3, 2, 3, 2},
        {2, 3},
        {2, 3}
    };
    private static int[][][] pBottom = {
        {{0, 0}},
        {{0}, {0, 0, 0, 0}},
        {{0, 0}, {0, 1, 1}, {2, 0}, {0, 0, 0}},
        {{0, 0}, {0, 0, 0}, {0, 2}, {1, 1, 0}},
        {{0, 1}, {1, 0, 1}, {1, 0}, {0, 0, 0}},
        {{0, 0, 1}, {1, 0}},
        {{1, 0, 0}, {0, 1}}
    };
    private static int[][][] pTop = {
        {{2, 2}},
        {{4}, {1, 1, 1, 1}},
        {{3, 1}, {2, 2, 2}, {3, 3}, {1, 1, 2}},
        {{1, 3}, {2, 1, 1}, {3, 3}, {2, 2, 2}},
        {{3, 2}, {2, 2, 2}, {2, 3}, {1, 2, 1}},
        {{1, 2, 2}, {3, 2}},
        {{2, 2, 1}, {2, 3}}
    };
}
