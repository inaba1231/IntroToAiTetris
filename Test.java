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
        //t.TestWeights();
    }

    public static final int POPULATION_SIZE = 1;
    public static final int SET_LENGTH = 21;

    public double[][] population;

    IO io;

    public void TestConvert() {
        State s = new State();
        for(int[] move : s.legalMoves()){
            int[][] bias = convert(move, s.nextPiece);
            p(bias);
        }
    }

    public int[][] convert(int[] move, int nextPiece) {
        int[][] bias = new int[4][SET_LENGTH];
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
        population = new double[POPULATION_SIZE][SET_LENGTH];

        //Random Initialisation
        for (int i = 0; i < POPULATION_SIZE; i++) {
            for (int j = 0; j < SET_LENGTH; j++) {
                population[i][j] = i + j;
            }
        }

        IO io = new IO();
        io.exportPopulation(population);

        double[][] population2 = io.importPopulation();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            for (int j = 0; j < SET_LENGTH; j++) {
                if (population[i][j] != population2[i][j]) {
                        System.out.println("Error");
                }
            }
        }
        System.out.println("No error");
    }
}
