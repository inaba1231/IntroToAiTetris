package Tetris;

import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author JunKiat
 */
public class DeepReinforcementLearning {

    public static final int ROWS = 21;
    public static final int COLS = 10;
    public static final int NODES = ROWS * COLS;
    
    public static double[][][] w1;
    public static double[] w2;
    public static double[] bias;
    //Bias is based on the move selected

    public DeepReinforcementLearning() {
        double[][][] w1 = new double[ROWS][COLS][NODES];
        double[] w2 = new double[NODES];
        double[] bias = new double[2];
        
        //Random Initialisation
        for (int i = 0; i < NODES; i++) {
            for (int j = 0; j < ROWS; j++) {
                for (int k = 0; k < COLS; k++) {
                    w1[j][k][i] = ThreadLocalRandom.current().nextDouble(-1, 1);
                }
            }
            w2[i] = ThreadLocalRandom.current().nextDouble(-1, 1);
        }
        bias[0] = ThreadLocalRandom.current().nextDouble(-1, 1);
        bias[1] = ThreadLocalRandom.current().nextDouble(-1, 1);
    }

    public int[] pickMove(State s) {
        int counter = s.legalMoves().length;
        double[] values = new double[counter];
        for (int i = 0; i < counter; i++) {
            double value = neural(s.getField(), s.legalMoves()[i]);
            if (value >= 0) {
                return s.legalMoves()[i];
            } else {
                values[i] = value;
            }
        }
        double max = -1;
        int move = 0;
        for (int i = 0; i < counter; i++) {
            if (values[i] > max) {
                max = values[i];
                move = i;
            }
        }
        return s.legalMoves()[move];
    }

    //Neural network that returns the probability of dropping in given state
    //as compared to changing state
    public double neural(int[][] field, int[] move) {
        //First layer
        double[] layer = new double[NODES];

        for (int i = 0; i < NODES; i++) {
            for (int j = 0; j < ROWS; j++) {
                for (int k = 0; k < COLS; k++) {
                    layer[i] += field[j][k] * w1[j][k][i];
                }
            }
            layer[i] += bias[0] * move[0] + bias[1] * move[1];

            //ReLU
            if (layer[i] < 0) {
                layer[i] = 0;
            }
        }

        //Second layer
        double value = 0;

        for (int i = 0; i < NODES; i++) {
            value += layer[i] * w2[i];
        }

        //Sigmoid
        return sigmoid(value);
    }

    public static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }
    
        
    public double error(){
        return 0;
    }

}
