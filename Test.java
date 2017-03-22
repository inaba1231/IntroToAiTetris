/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tetris;

import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author JunKiat
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Test t = new Test();
    }

    public static final int ROWS = 21;
    public static final int COLS = 10;
    public static final int NODES = ROWS * COLS;

    public double[][][] w1_;
    public double[] w2_;
    public double[][] bias_;

    Weights w;
    IO io;

    public Test() {
        w = new Weights();
        this.w1_ = new double[ROWS][COLS][NODES];
        this.w2_ = new double[NODES];
        this.bias_ = new double[2][NODES];

        //Random Initialisation
        for (int i = 0; i < NODES; i++) {
            for (int j = 0; j < ROWS; j++) {
                for (int k = 0; k < COLS; k++) {
                    w1_[j][k][i] = j + k + i;
                }
            }
            w2_[i] = i;
            bias_[0][i] = i;
            bias_[1][i] = i + 1;
        }

        IO io = new IO();
        w.w1_ = this.w1_;
        w.w2_ = this.w2_;
        w.bias_ = this.bias_;
        io.exportWeights(w);

        Weights w2 = io.importWeights();
        for (int i = 0; i < NODES; i++) {
            for (int j = 0; j < ROWS; j++) {
                for (int k = 0; k < COLS; k++) {
                    if (w.w1_[j][k][i] != w2.w1_[j][k][i]) {
                        System.out.println("Error");
                    }
                }
            }
            w2_[i] = i;
            bias_[0][i] = i;
            bias_[1][i] = i + 1;

            if (w.w2_[i] != w2.w2_[i]) {
                System.out.println("Error");
            }
            if (w.bias_[0][i] != w2.bias_[0][i]) {
                System.out.println("Error");
            }
            if (w.bias_[1][i] != w2.bias_[1][i]) {
                System.out.println("Error");
            }
        }
    }
}
