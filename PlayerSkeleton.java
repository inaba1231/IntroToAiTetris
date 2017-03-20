package Tetris;

import java.util.Random;

public class PlayerSkeleton {

    public static Heuristics h;
    public static MakeMove m;
    public static final int COLS = 10;
    public static final int ROWS = 21;
    public static final int size = 21;

    //Lucky guess
    public static double[] weight = {-2, -4, -6, -8, -10, -10, -8, -6, -4, -2, 8, 6, 4, 2, 2, -2, -4, -6, -8, -50, -40};
    //Zeroes
    //static double[] weight = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    //Temporary trained
    //static double[] weight = {-3.1000000000000005, -4.460000000000001, -5.850000000000002, -7.419999999999999, -9.379999999999999, -7.879999999999997, -7.47, -4.79, -7.989999999999998, -7.470000000000001, 6.200000000000002, 0.8799999999999997, 1.850000000000001, 2.2300000000000004, -0.10000000000000053, -0.9200000000000008, -2.3, -4.900000000000001, -10.23, -51.64000000000001, -38.67};

    public int[][] copy(int[][] a) {
        int[][] field = new int[ROWS][COLS];
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                field[j][i] = a[j][i];
            }
        }
        return field;
    }

    public static void main(String[] args) {
        DeepReinforcementLearning d = new DeepReinforcementLearning();
        for (int iter = 0; iter < 10; iter++) {
            State s = new State();
            while (!s.hasLost()) {
                //System.out.println(s.nextPiece);
                s.makeMove(d.pickMove(s));
            }
            System.out.println("Number of Rows Cleared: " + s.getRowsCleared());
        }
    }

//    public static void main(String[] args) {
//        h = new Heuristics(size, weight);
//        m = new MakeMove();
//        System.out.println(iteration());
//    }
    public static void StochasticLinear() {
        int iter = 10000;
        double step = 1;
        for (int a = 0; a < iter; a++) {
            Random random = new Random();
            int r = random.nextInt(size);
            int[] output = new int[3];
            output[0] = iteration();
            weight[r] = weight[r] + step;
            output[1] = iteration();
            weight[r] = weight[r] - step - step;
            output[2] = iteration();
            weight[r] = weight[r] + step;
            System.out.println(output[2] + " " + output[0] + " " + output[1]);
            if (output[1] <= output[0] && output[0] <= output[2]) {
                weight[r] = weight[r] - (step * output[2] / 100);
                System.out.println(output[2]);
            } else if (output[2] <= output[0] && output[0] <= output[1]) {
                weight[r] = weight[r] + (step * ((double) output[0]) / 100);
                System.out.println(output[1]);
            } else {
                System.out.println("No change");
            }
            for (double i : weight) {
                System.out.print(i + ", ");
            }
            System.out.println("");
        }
    }

    public static int iteration() {
        int loop = 25;
        int sum = 0;
        for (int i = 0; i < loop; i++) {
            State s = new State();
            PlayerSkeleton p = new PlayerSkeleton();
            while (!s.hasLost()) {
                System.out.println(s.nextPiece);
                p(s.legalMoves());
                s.makeMove(p.pickMove(s, s.legalMoves()));
            }
            //System.out.print(s.getRowsCleared() + " ");
            sum += s.getRowsCleared();
        }
        //System.out.println();
        //System.out.println(sum / loop);
        return sum / loop;
    }

    public int[] pickMove(State s, int[][] legalMoves) {
        double max = -Double.MAX_VALUE;
        int[] move = {0, 0};
        for (int[] x : legalMoves) {
            int[][] field = copy(s.getField());
            m.makeMove(field, x[0], x[1], s);
            double value = h.heuristic(field);
            //System.out.println(value);
            if (value > max) {
                max = value;
                move[0] = x[0];
                move[1] = x[1];
            }
        }
        return move;
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

}
