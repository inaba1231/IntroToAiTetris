package Tetris;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author JunKiat
 */
public class IO {

    public static final int ROWS = 21;
    public static final int COLS = 10;
    public static final int NODES = ROWS * COLS;
    public static final String filename = "weights.txt";

    public IO() {

    }

    public void exportWeights(Weights w) {
        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");

            //Print w1
            for (int i = 0; i < NODES; i++) {
                for (int j = 0; j < ROWS; j++) {
                    for (int k = 0; k < COLS; k++) {
                        writer.print(w.w1_[j][k][i] + ",");
                    }
                    writer.println();
                }
            }

            //Print w2
            for (int i = 0; i < NODES; i++) {
                writer.print(w.w2_[i] + ",");
            }
            writer.println();

            //Print bias
            for (int i = 0; i < NODES; i++) {
                for (int j = 0; j < 4; j++) {
                    for (int k = 0; k < COLS; k++) {
                        writer.print(w.bias_[j][k][i] + ",");
                    }
                    writer.println();
                }
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {

        }
    }

    public Weights importWeights() {
        Weights w = new Weights();
        w.w1_ = new double[ROWS][COLS][NODES];
        w.w2_ = new double[NODES];
        w.bias_ = new double[4][COLS][NODES];
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String[] line;
            
            //Read w1
            for (int i = 0; i < NODES; i++) {
                for (int j = 0; j < ROWS; j++) {
                    line = br.readLine().split(",");
                    for (int k = 0; k < COLS; k++) {
                        w.w1_[j][k][i] = Double.parseDouble(line[k]);
                    }
                }
            }
            
            //Read w2
            line = br.readLine().split(",");
            for (int i = 0; i < NODES; i++) {
                w.w2_[i] = Double.parseDouble(line[i]);
            }
            
            //Read bias
            for (int i = 0; i < NODES; i++) {
                for (int j = 0; j < 4; j++) {
                    line = br.readLine().split(",");
                    for (int k = 0; k < COLS; k++) {
                        w.bias_[j][k][i] = Double.parseDouble(line[k]);
                    }
                }
            }
        } catch (Exception e) {

        }
        return w;
    }
}
