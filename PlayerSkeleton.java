package Tetris;

import java.util.Random;

import static Tetris.Constants.*;

public class PlayerSkeleton {

    public static Heuristics h;
    public static MakeMove m;
    public static Random nature;

    public PlayerSkeleton(double[] set) {
        h = new Heuristics(set.length, set);
        m = new MakeMove();
        nature = new Random();
    }

    public int[] pickMove(State s, int[][] legalMoves) {
        double max = -Double.MAX_VALUE;
        int[] move = {0, 0};
        for (int[] x : legalMoves) {
            int[][] field = copy(s.getField());
            m.makeMove(field, x[0], x[1], s);
            double value = h.heuristic(field);
            if (value > max) {
                max = value;
                move[0] = x[0];
                move[1] = x[1];
            }
        }
        return move;
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

    private static int[] getCumulativeFitness(double[][] population) {
        int totalFitness = 0;
        int[] cumulativeFitness = new int[population.length];

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

            System.out.println("Set " + i + " completed " + s.getRowsCleared() + " rows.");
            totalFitness += s.getRowsCleared();
            cumulativeFitness[i] = totalFitness;
        }

        return cumulativeFitness;
    }

    private static int binarySearch(int[] array, int number) {
        return binarySearch(array, number, 0, array.length - 1);
    }

    private static int binarySearch(int[] array, int number, int left, int right) {
        if (left == right) {
            return left;
        }

        int mid = (right - left)/2;
        if (array[mid] > number) {
            return binarySearch(array, number, left, mid);
        } else {
            return binarySearch(array, number, mid + 1, right);
        }
    }

    private static double[][] select(double[][] population, int[] cumulativeFitness) {
        int totalFitness = cumulativeFitness[cumulativeFitness.length - 1];
        if (totalFitness == 0) {
            return BigBang.resetPopulation();
        }

        double[][] nextPopulation = new double[POPULATION_SIZE][SET_LENGTH];
        for (int i = 0; i < nextPopulation.length; i++) {
            int randomNumber = nature.nextInt(totalFitness);
            nextPopulation[i] = population[binarySearch(cumulativeFitness, randomNumber)];
        }
        return nextPopulation;
    }

    private static void crossOver(double[][] population) {
        for (int i = 1; i < population.length; i++) {
            int crossOverPoint = nature.nextInt(population[i].length);
            for (int j = crossOverPoint; j < population[i].length; j++) {
                double temp = population[i - 1][j];
                population[i - 1][j] = population[i][j];
                population[i][j] = temp;
            }
        }
    }

    private static void mutate(double[][] population) {
        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < population[i].length; j++) {
                if (nature.nextDouble() < MUTATION_RATE) {
                    double mutation = nature.nextDouble();
                    if (nature.nextBoolean()) {
                        population[i][j] = mutation;
                    } else {
                        population[i][j] = -mutation;
                    }
                }
            }
        }
    }

    private static void runAlgo(int cycles) {
        if (cycles < 1) {
            return;
        }

        IO io = new IO();
        double[][] population = io.importPopulation();

        for (int i = 0; i < cycles; i++) {
            int[] cumulativeFitness = getCumulativeFitness(population);
            population = select(population, cumulativeFitness);
            crossOver(population);
            mutate(population);
        }

        io.exportPopulation(population);
    }

    public static void main(String[] args) {
        //BigBang.resetPopulation();
        
        runAlgo(1);
    }

}
