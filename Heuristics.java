package Tetris;

import static Tetris.Constants.ROWS;

import java.util.Arrays;

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

    
    
    public double heuristic(State s) {
        
        double[] feature = new double[size];
    
	
	// 0 roughness
        int[] top = s.getTop();
	int roughness = 0;
	for (int i = 0; i < top.length - 1; ++i) {
		roughness += Math.abs(top[i] - top[i + 1]);
	}
	feature[0] = -(float) roughness;
	
	// 1 max column height
	int maxHeight = Integer.MIN_VALUE;
	for (int column = 0; column < top.length; ++column) {
		int height = top[column];
		if (height > maxHeight) {
			maxHeight = height;
		}
	}

	feature[1] = -(float) maxHeight;
	
	// 2 number of rows cleared
	feature[2] = s.getRowsCleared();
	
	// 3 whether game has been lost
	feature[3] = s.hasLost() ? -10.0 : 10.0;
	
	// 4 number of faults
	int[][] field = s.getField();
	int numFaults = 0;

	for (int x = 0; x < State.COLS; ++x) {
		for (int y = top[x] - 1; y >= 0; --y) {
			if (field[y][x] == 0) {
				++numFaults;
			}
		}
	}
	feature[4] =  -(float) numFaults;
	
	// 5 pit depths
	int sumOfPitDepths = 0;
	int pitHeight;
	int leftOfPitHeight;
	int rightOfPitHeight;

	// pit depth of first column
	pitHeight = top[0];
	rightOfPitHeight = top[1];
	int diff = rightOfPitHeight - pitHeight;
	if (diff > 2) {
		sumOfPitDepths += diff;
	}

	for (int col = 0; col < State.COLS - 2; col++) {
		leftOfPitHeight = top[col];
		pitHeight = top[col + 1];
		rightOfPitHeight = top[col + 2];

		int leftDiff = leftOfPitHeight - pitHeight;
		int rightDiff = rightOfPitHeight - pitHeight;
		int minDiff = leftDiff < rightDiff ? leftDiff : rightDiff;

		if (minDiff > 2) {
			sumOfPitDepths += minDiff;
		}
	}

	// pit depth of last column
	pitHeight = top[State.COLS - 1];
	leftOfPitHeight = top[State.COLS - 2];
	diff = leftOfPitHeight - pitHeight;
	if (diff > 2) {
		sumOfPitDepths += diff;
	}

	feature[5] = -(float) sumOfPitDepths;
	
	// 6 mean height difference
	int sum = 0;
	for (int height : top) {
		sum += height;
	}

	float meanHeight = (float) sum / top.length;

	float avgDiff = 0;
	for (int height : top) {
		avgDiff += Math.abs(meanHeight - height);
	}

	feature[6] = -(avgDiff / (float) top.length);

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
