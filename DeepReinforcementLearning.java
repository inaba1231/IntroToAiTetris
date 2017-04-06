package Tetris;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author JunKiat
 */
public class DeepReinforcementLearning {

    public static final int ROWS = 21;
    public static final int COLS = 10;
    public static final int NODES = ROWS * COLS;

    public double[][][] w1_;
    public double[] w2_;
    public double[][][] bias_;
    //Bias is of size 4 * 10 based on the selected piece and orientation

    DecimalFormat df = new DecimalFormat("#.####");
    Weights w;
    IO io;
    public int maxRowsCleared = 1;
    private LinkedList<Move> moveList = new LinkedList();

    public DeepReinforcementLearning(boolean reset) {
        io = new IO();
        if (reset) {
            w = new Weights();
            this.w1_ = new double[ROWS][COLS][NODES];
            this.w2_ = new double[NODES];
            this.bias_ = new double[4][COLS][NODES];

            //Random Initialisation
            for (int i = 0; i < NODES; i++) {
                for (int j = 0; j < ROWS; j++) {
                    for (int k = 0; k < COLS; k++) {
                        w1_[j][k][i] = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
                    }
                }
                w2_[i] = ThreadLocalRandom.current().nextDouble(-1, 1);
                for (int j = 0; j < 4; j++) {
                    for (int k = 0; k < COLS; k++) {
                        bias_[j][k][i] = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
                    }
                }
            }
        } else {
            w = io.importWeights();
            this.w1_ = w.w1_;
            this.w2_ = w.w2_;
            this.bias_ = w.bias_;
        }
    }

    public int[] pickMove(State s) {
        int counter = s.legalMoves().length;
        double[] values = new double[counter];
        int[][] field = s.getField();
        for (int j = 0; j < ROWS; j++) {
            for (int k = 0; k < COLS; k++) {
                if (field[j][k] != 0) {
                    field[j][k] = 1;
                }
            }
        }
        for (int i = 0; i < counter; i++) {
            double value = neural(field, convert(s.legalMoves()[i], s.getNextPiece()));
            //System.out.println("value: " + df.format(value));

            //If there is a larger than 50% chance of dropping, we return the move
            //else switch move
            if (value >= 0.5) {
                return s.legalMoves()[i];
            } else {
                values[i] = value;
            }
        }

        //In the case where it is always switching move,
        //We will choose the one with highest probability of dropping
        double max = -1;
        int move = 0;
        for (int i = 0; i < counter; i++) {
            if (values[i] > max) {
                max = values[i];
                move = i;
            }
        }
        //System.out.println("FULL CLEAR");
        return s.legalMoves()[move];
    }

    //Neural network that returns the probability of dropping in given state
    //as compared to changing state
    public double neural(int[][] field, int[][] move) {
        //First layer
        double[] layer = new double[NODES];

        for (int i = 0; i < NODES; i++) {
            for (int j = 0; j < ROWS; j++) {
                for (int k = 0; k < COLS; k++) {
                    layer[i] += field[j][k] * w1_[j][k][i];
                }
            }
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < COLS; k++) {
                    layer[i] += bias_[j][k][i] * move[j][k];
                }
            }

            //ReLU
            //if (layer[i] < 0) {
             //   layer[i] = 0;
            //}
        }

        //Second layer
        double value = 0;

        for (int i = 0; i < NODES; i++) {
            
            layer[i] = sigmoid(layer[i]);
            value += layer[i] * w2_[i];
            
        }
        value = sigmoid(value);
        moveList.add(new Move(field, move, layer, value));

        //Sigmoid
        return value;
    }


    public void updateWeights(int currRowsCleared) {
        if(currRowsCleared>this.maxRowsCleared) {
            this.maxRowsCleared = currRowsCleared;
        }
        double payoff;
        if (currRowsCleared>10) {
            payoff = error(currRowsCleared);
        } else {
            payoff  = calculateInitialPayoff(currRowsCleared);
            System.out.println(payoff);
        }
        payoff = sigmoid(payoff);
        backwardPropagation(moveList.size(), moveList, payoff, this.w1_,this.w2_,this.bias_);

        w.w1_ = this.w1_;
        w.w2_ = this.w2_;
        w.bias_ = this.bias_;
        io.exportWeights(w);
        moveList.clear();
    }

    private double calculateInitialPayoff(int currRowsCleared) {
        Move secondLastMove = this.moveList.get(this.moveList.size()-1);
        int[][] field = secondLastMove.getInputLayer();
        int[] heights = new int[10];
        int aggregateHeight = 0;
        
        for (int i = 0; i < 10; i++) {
            heights[i] = colHeight(field, i);
            aggregateHeight += heights[i];
        }
        
        int holes = 0;
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS - 2; j++) {
                if (field[j][i] == 0 && j < heights[i]) {
                    holes++;
                }
            }
        }
        
        int bumpiness = 0;
        for(int i=0; i<9; i++) {
            bumpiness += Math.abs(heights[i] - heights[i+1]);
        }
        
        int gridsFilled = 0;
        
        for(int i=0; i<21; i++) {
            for(int j = 0; j<10; j++) {
                if(field[i][j] > 0) {
                    gridsFilled++;
                }
            }
        }
        
        double colsProportion = 0;
        
        for(int i=0; i<9; i++) {
            if(heights[i]>0) {
                colsProportion++;
            }
        }
        colsProportion = colsProportion/10;
        
        if(colsProportion<=0.7) {
            colsProportion = colsProportion*-1;
        }
        
        
        return 0.45*colsProportion+ 0.5*(aggregateHeight-55) + 0.76*currRowsCleared - 0.3*holes - 0.5*bumpiness + 0.2*gridsFilled;
        
    }
    
    public int colHeight(int[][] field, int col) {
        for (int x = ROWS - 2; x >= 0; x--) {
            if (field[x][col] != 0) {
                return x + 1;
            }
        }
        return 0;
    }

    /* Updates the set of weights w1,bias,w2 to a new better set of weights after carrying out backward propagation through every move.
     * 
     * n is the number of moves that were played in this game
     * 
     * Inputs is the data structure containing all 250 inputs to the input nodes
     * Example: in.getInputLayer(n,move) gives value for input node n for a certain move i. (move starts from 1 to last move)
     * 			first 40n must be the bias nodes, rest of the 210n are the w1 nodes
     * 
     * Outputs is the object containing all 210 outputs from the hidden layer nodes and the output of the final node for every move.
     * Example: out.getHiddenLayer(n,move) gives output of hidden layer node n for a certain move i. 
     * 			out.getFinalOutput(move) gives the output of the final node for a certain move i. (move starts from 1 to last move)
     * 
     * payoff is the payoff achieved at the end of this game
     */

    public void backwardPropagation(int n,LinkedList<Move> moveList, double payoff, double[][][] w1_, double[] w2_, double[][][] bias_) {
        int learning_rate = 5;
        double[][][] current_w1 = w1_;
        double[][][] current_bias = bias_;
        double[] current_w2 = w2_;

        for (int i = n - 1; i >= 0; i--) { //for every move starting from the last move
            Move currMove = moveList.get(i);
            //keep a copy of previous hidden layer weights
            double[] oldw2 = new double[current_w2.length];
            System.arraycopy(current_w2, 0, oldw2, 0, current_w2.length);

            //Update current weights after doing calculation for this move        
            //update outer layer weights
            double do1 = payoff;
            double do2 = currMove.getFinalOutput() * (1 - currMove.getFinalOutput());
            for (int j = 0; j < 210; j++) {
                double do3 = currMove.getHiddenLayer()[j];
                current_w2[j] = current_w2[j] - (do1 * do2 * do3);
            }

            //update inner layer weights
            double dh11 = do1 * do2;
            for (int k = 0; k < 25; k++) { // for every input node
                for (int m = 0; m < 10; m++) {
                    double dh3;
                    if (k < 4) { //bias nodes
                        dh3 = currMove.getBias()[k][m];
                    } else {
                        dh3 = currMove.getInputLayer()[k - 4][m];
                    }
                    for (int l = 0; l < 210; l++) { // for every outgoing edge of that node
                        double dh1;
                        double dh2;
                        double dh12 = oldw2[l];

                        dh1 = dh11 * dh12;
                        dh2 = currMove.getHiddenLayer()[l] * (1 - currMove.getHiddenLayer()[l]);

                        // update bias weights
                        if (k < 4) {
                            current_bias[k][m][l] = current_bias[k][m][l] - (dh1 * dh2 * dh3);
                        } else { // update w1
                            current_w1[k - 4][m][l] = current_w1[k - 4][m][l] - (dh1 * dh2 * dh3);
                        }
                    }
                }
            }
        }

        this.w1_ = current_w1;
        this.w2_ = current_w2;
        this.bias_ = current_bias;
    }

    public int error(int rowsCleared) {
        return rowsCleared - maxRowsCleared;
    }

    public static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
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
