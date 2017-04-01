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

        double[][] population = new double[POPULATION_SIZE][SET_LENGTH];

        for (int i = 0; i < POPULATION_SIZE; i++) {
            for (int j = 0; j < SET_LENGTH; j++) {
                population[i][j] = randomWeight();
            }
        }

        return population;

    }

    public static double randomWeight() {
        Random nature = new Random();
        double weight = nature.nextDouble();
        if (nature.nextBoolean()) {
            return weight;
        } else {
            return -weight;
        }
    }
}
