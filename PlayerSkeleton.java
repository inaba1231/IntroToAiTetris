package Tetris;

public class PlayerSkeleton {

    public static Heuristics h;
    public static MakeMove m;
    public static final int COLS = 10;
    public static final int ROWS = 21;
    public static final int size = 21;

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
        DeepReinforcementLearning d = new DeepReinforcementLearning(false);
        for (int iter = 0; iter < 100; iter++) {
            State s = new State();
            TFrame frame = new TFrame(s);
            while (!s.hasLost()) {
                s.makeMove(d.pickMove(s));
                s.draw();
                s.drawNext(0, 0);
            }
            frame.dispose();
            int currRowsCleared = s.getRowsCleared();

            System.out.println(iter + " Number of Rows Cleared: " + currRowsCleared);
            d.updateWeights(currRowsCleared);
        }
    }

    public int[] pickMove(State s, int[][] legalMoves) {
        double max = -Double.MAX_VALUE;
        int[] move = {0, 0};
        for (int[] x : legalMoves) {
            int[][] field = copy(s.getField());
            m.makeMove(field, x[0], x[1], s);
            double value = h.heuristic(field);
            //System.out.println(value);
            if (value > max) {
                max = value;
                move[0] = x[0];
                move[1] = x[1];
            }
        }
        return move;
    }
}
