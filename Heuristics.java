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
    
	
	// 0 height difference between all pairs
        int[] top = s.getTop();
	int heightDiff = 0;
	for (int i = 0; i < top.length - 1; ++i) {
		heightDiff += Math.abs(top[i] - top[i + 1]);
	}
	feature[0] = heightDiff;
	
	// 1 max column height
	int maxHeight = Integer.MIN_VALUE;
	for (int column = 0; column < top.length; column++) {
		int height = top[column];
		if (height > maxHeight) {
			maxHeight = height;
		}
	}

	feature[1] = maxHeight;
	
	// 2 number of rows cleared
	feature[2] = s.getRowsCleared();
	
	// 3 whether game has been lost
	feature[3] = s.hasLost() ? -1 : 1;
	
	// 4 number of holes
	int[][] field = s.getField();
	int numHoles = 0;

	for (int col = 0; col < Constants.COLS; col++) {
		for (int row = top[col] - 1; row >= 0; row--) {
			if (field[row][col] == 0) {
				numHoles++;
			}
		}
	}
	feature[4] =  numHoles;
	
	// 5 pit depths
	int sumOfPitDepths = 0;
	
	int heightOfCol;
	int heightOfLeftCol;
	int heightOfRightCol;

	// pit depth of first column
	heightOfCol = top[0];
	heightOfRightCol = top[1];
	int heightDifference = heightOfRightCol - heightOfCol;
	if (heightDifference > 2) {
		sumOfPitDepths += heightDifference;
	}

	for (int col = 0; col < Constants.COLS - 2; col++) {
		heightOfLeftCol = top[col];
		heightOfCol = top[col + 1];
		heightOfRightCol = top[col + 2];

		int leftHeightDifference = heightOfLeftCol - heightOfCol;
		int rightHeightDifference = heightOfRightCol - heightOfCol;
		int minDiff = Math.min(leftHeightDifference, rightHeightDifference);

		if (minDiff > 2) {
			sumOfPitDepths += minDiff;
		}
	}

	// pit depth of last column
	heightOfCol = top[Constants.COLS - 1];
	heightOfLeftCol = top[Constants.COLS - 2];
	heightDifference = heightOfLeftCol - heightOfCol;
	if (heightDifference > 2) {
		sumOfPitDepths += heightDifference;
	}

	feature[5] = sumOfPitDepths;
	
	// 6 weighted height difference
	int totalHeight = 0;
	for (int height : top) {
		totalHeight += height;
	}

	double meanHeight = (double) totalHeight / top.length;

	double avgDiff = 0;
	
	for (int height : top) {
		avgDiff += Math.abs(meanHeight - height);
	}

	feature[6] = avgDiff / top.length;

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
