/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tetris;

/**
 *
 * @author JunKiat
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Test t = new Test();
    }

    public static final int ROWS = 21;
    public static final int COLS = 10;
    public static final int NODES = ROWS * COLS;

    public double[][][] w1_;
    public double[] w2_;
    public double[][][] bias_;

    Weights w;
    IO io;

    public Test() {
    }

    public void TestConvert() {
        State s = new State();
        for(int[] move : s.legalMoves()){
            int[][] bias = convert(move, s.nextPiece);
            p(bias);
        }
    }

    public int[][] convert(int[] move, int nextPiece) {
        int[][] bias = new int[4][COLS];
        int width = pWidth[nextPiece][move[0]];
        for (int i = 0; i < width; i++) {
            for (int j = pBottom[nextPiece][move[0]][i]; j < pTop[nextPiece][move[0]][i]; j++) {
                bias[j][i + move[1]] = 1;
            }
        }
        return bias;
    }

    public static void p(int[][] a) {
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

    public void TestWeights() {
        w = new Weights();
        this.w1_ = new double[ROWS][COLS][NODES];
        this.w2_ = new double[NODES];
        this.bias_ = new double[4][COLS][NODES];

        //Random Initialisation
        for (int i = 0; i < NODES; i++) {
            for (int j = 0; j < ROWS; j++) {
                for (int k = 0; k < COLS; k++) {
                    w1_[j][k][i] = j + k + i;
                }
            }
            w2_[i] = i;
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < COLS; k++) {
                    bias_[j][k][i] = j + k + i;
                }
            }
        }

        IO io = new IO();
        w.w1_ = this.w1_;
        w.w2_ = this.w2_;
        w.bias_ = this.bias_;
        io.exportWeights(w);

        Weights w2 = io.importWeights();
        for (int i = 0; i < NODES; i++) {
            for (int j = 0; j < ROWS; j++) {
                for (int k = 0; k < COLS; k++) {
                    if (w.w1_[j][k][i] != w2.w1_[j][k][i]) {
                        System.out.println("Error");
                    }
                }
            }

            if (w.w2_[i] != w2.w2_[i]) {
                System.out.println("Error");
            }

            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < COLS; k++) {
                    if (w.bias_[j][k][i] != w2.bias_[j][k][i]) {
                        System.out.println("Error");
                    }
                }
            }
        }
    }
}
