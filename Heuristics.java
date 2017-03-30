package Tetris;

import static Tetris.Constants.*;

/**
 *
 * @author JunKiat
 */
public class Heuristics {

    public static int size;
    public static double[] weight;

    public Heuristics(int size, double[] weight) {
        this.size = size;
        this.weight = weight;
    }

    public void updateWeight(double[] weight) {
        this.weight = weight;
    }

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
        
        // 21    num filled spots (non-weighted)
        int filledSpots = 0;
        for (int row = 0; row < ROWS; row++) {
        	for (int col = 0; col < COLS; col++) {
        		if (field[row][col] != 0) {
        			filledSpots += row;
        		}
        	}
        }
        feature[21] = filledSpots;
        
        // 22	num filled spots (weighted)
        int weightedFilledSpots = 0;
        for (int row = 0; row < ROWS; row++) {
        	for (int col = 0; col < COLS; col++) {
        		if (field[row][col] != 0) {
        			weightedFilledSpots += row;
        		}
        	}
        }
        feature[22] = weightedFilledSpots;
        
        // 23 num of lines cleared
        int linesCleared = 0;
        for (int row = 0; row < ROWS; row++) {
        	int filledColumns = 0;
        	for (int col = 0; col < COLS; col++) {
        		if (field[row][col] == 0) {
        			break;
        		}
        		filledColumns += 1;
        	}
        	if (filledColumns == COLS) {
        		linesCleared += 1;
        	} else if (filledColumns == 0) {
        		break;
        	}
        }
        feature[23] = linesCleared;

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
}
