package Tetris;

import java.util.Random;

public class PlayerSkeleton {

    public static Heuristics h;
    public static MakeMove m;
    public static IO io;
    public static final int COLS = 10;
    public static final int ROWS = 21;
    public static final int size = 21;

    public PlayerSkeleton(double[] set) {
        h = new Heuristics(set.length, set);
        m = new MakeMove();
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
/*
    public static void main(String[] args) {
        DeepReinforcementLearning d = new DeepReinforcementLearning(true);
        for (int iter = 0; iter < 10; iter++) {
            State s = new State();
            while (!s.hasLost()) {
                s.makeMove(d.pickMove(s));
            }
            System.out.println("Number of Rows Cleared: " + s.getRowsCleared());
            d.updateWeights();
        }
    }
    */

    public static void main(String[] args) {
        IO io = new IO();
        double[][] population = io.importPopulation();
        for (int i = 0; i < population.length; i++) {
            State s = new State();
            new TFrame(s);
            double[] set = population[i];
            PlayerSkeleton p = new PlayerSkeleton(set);
            while (!s.hasLost()) {
                s.makeMove(p.pickMove(s, s.legalMoves()));
                s.draw();
                s.drawNext(0, 0);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("#" + i + ": You have completed " + s.getRowsCleared() + " rows.");
        }
    }

//    public static void main(String[] args) {
//        h = new Heuristics(size, weight);
//        m = new MakeMove();
//        System.out.println(iteration());
//    }
    /*
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
    */

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
