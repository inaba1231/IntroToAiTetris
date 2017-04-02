/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tetris;

/**
 *
 * @author JunKiat
 */
public class Move {

    private int[][] inputLayer_;
    private int[][] bias_;
    private double[] hiddenLayer_;
    private double finalOutput_;

    public Move(int[][] inputLayer, int[][] bias, double[] hiddenLayer, double finalOutput) {
        inputLayer_ = inputLayer;
        bias_ = bias;
        hiddenLayer_ = hiddenLayer;
        finalOutput_ = finalOutput;
    }

    public int[][] getInputLayer() {
        return inputLayer_;
    }

    public int[][] getBias() {
        return bias_;
    }

    public double[] getHiddenLayer() {
        return hiddenLayer_;
    }

    public double getFinalOutput() {
        return finalOutput_;
    }
}
