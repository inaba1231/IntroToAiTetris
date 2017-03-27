package Tetris;

import java.util.Random;

import static Tetris.Constants.*;

/**
 * Created by kazuhiro on 27/3/17.
 *
 * Generate random initial population.
 */
public class BigBang {

    public static double[][] resetPopulation() {

        Random nature = new Random();
        double[][] population = new double[POPULATION_SIZE][SET_LENGTH];

        for (int i = 0; i < POPULATION_SIZE; i++) {
            for (int j = 0; j < SET_LENGTH; j++) {
                double weight = nature.nextDouble();
                if (nature.nextBoolean()) {
                    population[i][j] = weight;
                } else {
                    population[i][j] = -weight;
                }
            }
        }

        return population;

    }

    /* Initialize first generation of population
    public static void main(String args[]) {

        IO io = new IO();
        io.exportPopulation(resetPopulation());

    }
    */



}
