package Tetris;

public class PlayerSkeleton {

    public static Heuristics h;
    public static MakeMove m;
    public static final int COLS = 10;
    public static final int ROWS = 21;
    public static final int size = 21;

    public PlayerSkeleton(double[] set) {
        h = new Heuristics(set.length, set);
        m = new MakeMove();
    }

    public int[] pickMove(State s, int[][] legalMoves) {
        double max = -Double.MAX_VALUE;
        int[] move = {0, 0};
        for (int[] x : legalMoves) {
            int[][] field = copy(s.getField());
            m.makeMove(field, x[0], x[1], s);
            double value = h.heuristic(field);
            if (value > max) {
                max = value;
                move[0] = x[0];
                move[1] = x[1];
            }
        }
        return move;
    }

    public int[][] copy(int[][] a) {
        int[][] field = new int[ROWS][COLS];
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                field[j][i] = a[j][i];
            }
        }
        return field;
    }

    public static void main(String[] args) {
        IO io = new IO();
        double[][] population = io.importPopulation();
        for (int i = 0; i < population.length; i++) {
            State s = new State();
            new TFrame(s);
            double[] set = population[i];
            PlayerSkeleton p = new PlayerSkeleton(set);
            while (!s.hasLost()) {
                s.makeMove(p.pickMove(s, s.legalMoves()));
                s.draw();
                s.drawNext(0, 0);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("#" + i + ": You have completed " + s.getRowsCleared() + " rows.");
        }
    }

}
