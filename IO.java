package Tetris;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import static Tetris.Constants.*;

/**
 *
 * @author JunKiat
 */
public class IO {

    public void exportPopulation(double[][] population) {
        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");

            writer.print(population.length + ",");
            writer.println(population[0].length + ",");
            for (int i = 0; i < POPULATION_SIZE; i++) {
                for (int j = 0; j < SET_LENGTH; j++) {
                    writer.print(population[i][j] + ",");
                }
                writer.println();
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {

        }
    }

    public double[][] importPopulation() {
        double[][] population = new double[POPULATION_SIZE][SET_LENGTH];

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String[] line;
            line = br.readLine().trim().split(",");
            int inputPopulationSize = Integer.parseInt(line[0]);
            int inputSetLength = Integer.parseInt(line[1]);

            for (int i = 0; i < POPULATION_SIZE; i++) {
                if (i < inputPopulationSize) {
                    line = br.readLine().split(",");
                    for (int j = 0; j < SET_LENGTH; j++) {
                        if (j < inputSetLength) {
                            population[i][j] = Double.parseDouble(line[j]);
                        } else {
                            population[i][j] = BigBang.randomWeight();
                        }
                    }
                } else {
                    for (int j = 0; j < SET_LENGTH; j++) {
                        population[i][j] = BigBang.randomWeight();
                    }
                }
            }
        } catch (Exception e) {

        }
        return population;
    }
}
