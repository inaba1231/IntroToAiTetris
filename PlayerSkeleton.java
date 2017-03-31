package Tetris;

import java.util.Arrays;
import java.util.Random;

import static Tetris.Constants.*;

public class PlayerSkeleton {

    public static Heuristics h;
    public static MakeMove m;
    public static LegalMoves l;
    public static Random nature;

    public PlayerSkeleton(double[] set) {
        h = new Heuristics(set.length, set);
        m = new MakeMove();
        l = new LegalMoves();
        nature = new Random();
    }

    public double expectimaxAlgo(State s, int[][] field){
    	double sum = 0;
    	for(int i = 0; i<7; i++)}{
			for(int[] possibleMove: l.legalMoves[i]){
				int[][] helper = copy(field);
				//todo
				//deal with s.getTop();
				m.makeMove(helper, possibleMove[0], possibleMove[1], i, s.getTop());
				sum += h.heuristic(helper);
			}
		}
		return sum;
    }

	public double minimaxAlgo(State s, int[][] field){
		double min = Double.MAX_VALUE;
		double score = 0;
		for(int i=0; i<7;i++){
			for(int[] possibleMove: l.legalMoves[i]){
				//todo implement alpha-beta pruning
				int[][] helper = copy(field);
				m.makeMove(helper, possibleMove[0], possibleMove[1],i, s.getTop());
				score = h.heuristic(helper);
				if(score<min){
					min = score;
				}
			}
		}
		return min;
	}
    
    public int[] pickMove(State s, int[][] legalMoves) {
        double max = -Double.MAX_VALUE;
        int[] move = {0, 0};
        for (int[] x : legalMoves) {
            int[][] field = copy(s.getField());
            m.makeMove(field, x[0], x[1], s.getNextPiece(), s.getTop());
            //l.legalMoves
			
			//Expectimax algorithm
			double value = expectimaxAlgo(s, field);
			
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
        int bestScore = 0;

        for (int i = 0; i < population.length; i++) {
            State s = new State();
            //TFrame frame = new TFrame(s);
            double[] set = population[i];
            PlayerSkeleton p = new PlayerSkeleton(set);

            while (!s.hasLost()) {
                s.makeMove(p.pickMove(s, s.legalMoves()));
                /*
                s.draw();
                s.drawNext(0, 0);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                */
            }

            //frame.dispose();
            //System.out.println("Set " + i + " completed " + s.getRowsCleared() + " rows.");
            bestScore = bestScore < s.getRowsCleared() ? s.getRowsCleared() : bestScore;
            totalFitness += s.getRowsCleared();
            cumulativeFitness[i] = totalFitness;
        }

        System.out.println(bestScore);

        return cumulativeFitness;
    }

    private static int binarySearch(int[] array, int number) {
        return binarySearch(array, number, 0, array.length - 1);
    }

    private static int binarySearch(int[] array, int number, int left, int right) {
        if (left == right) {
            return left;
        }

        int mid = (right - left)/2 + left;
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
            int selectedSet = binarySearch(cumulativeFitness, randomNumber);
            nextPopulation[i] = Arrays.copyOf(population[selectedSet], population[selectedSet].length);
            //System.out.println("Set " + selectedSet + " has been selected.");
        }
        return nextPopulation;
    }

    private static void crossOver(double[][] population) {
        for (int i = 1; i < population.length; i = i + 2) {
            int crossOverPoint = nature.nextInt(population[i].length);
            for (int j = crossOverPoint; j < population[i].length; j++) {
                double temp = population[i - 1][j];
                population[i - 1][j] = population[i][j];
                population[i][j] = temp;
            }
            //System.out.println("Cross over point for new sets " + (i-1) + " and " + i + " is " + crossOverPoint + ".");
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
                    //System.out.println("Mutation occurs in new set " + i + " at index " + j + " with mutation value: " + mutation + ".");
                }
            }
        }
    }

    private static void runAlgo(int cycles) {
        if (cycles < 1) {
            return;
        }

        IO io = new IO();
        if (io.importPopulation().length != POPULATION_SIZE) {
            io.exportPopulation(BigBang.resetPopulation());
        }

        for (int i = 0; i < cycles; i++) {
            double[][] population = io.importPopulation();
            System.out.print("Best score in generation " + (i + 1) + " is: ");
            int[] cumulativeFitness = getCumulativeFitness(population);
            double[][] nextPopulation = select(population, cumulativeFitness);
            crossOver(nextPopulation);
            mutate(nextPopulation);
            io.exportPopulation(nextPopulation);
        }
    }

    public static void main(String[] args) {
        runAlgo(CYCLE_COUNT);
        System.out.println("Done!");
    }

}
