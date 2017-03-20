package Tetris;

/**
 *
 * @author JunKiat
 */
public class Heuristics {

    public static final int COLS = 10;
    public static final int ROWS = 21;
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
