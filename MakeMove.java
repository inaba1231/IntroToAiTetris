package Tetris;

import static Tetris.Constants.*;

/**
 *
 * @author JunKiat
 */
public class MakeMove {

    public void makeMove(int[][] field, int orient, int slot, int nextPiece, int[] sTop) {
        
        int[] top = new int[COLS];
        System.arraycopy(sTop, 0, top, 0, COLS);

        //height if the first column makes contact
        int height = top[slot] - pBottom[nextPiece][orient][0];
        //for each column beyond the first in the piece
        for (int c = 1; c < pWidth[nextPiece][orient]; c++) {
            height = Math.max(height, top[slot + c] - pBottom[nextPiece][orient][c]);
        }

        //check if game ended
        if (height + pHeight[nextPiece][orient] >= ROWS) {
            field[0][0] = -1;
            return;
        }

        //for each column in the piece - fill in the appropriate blocks
        for (int i = 0; i < pWidth[nextPiece][orient]; i++) {

            //from bottom to top of brick
            for (int h = height + pBottom[nextPiece][orient][i]; h < height + pTop[nextPiece][orient][i]; h++) {
                field[h][i + slot] = 1;
            }
        }

        //check for full rows - starting at the top
        for (int r = 20; r >= 0; r--) {
            //check all columns in the row
            boolean full = true;
            for (int c = 0; c < COLS; c++) {
                if (field[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            //if the row was full - remove it and slide above stuff down
            if (full) {
                //for each column
                for (int c = 0; c < COLS; c++) {

                    //slide down all bricks
                    for (int i = r; i < 20; i++) {
                        field[i][c] = field[i + 1][c];
                    }
                }
            }
        }
    }

    //possible orientations for a given piece type
    protected static int[] pOrients = {1, 2, 4, 4, 4, 2, 2};

    //the next several arrays define the piece vocabulary in detail
    //width of the pieces [piece ID][orientation]
    protected static int[][] pWidth = {
        {2},
        {1, 4},
        {2, 3, 2, 3},
        {2, 3, 2, 3},
        {2, 3, 2, 3},
        {3, 2},
        {3, 2}
    };
    //height of the pieces [piece ID][orientation]
    private static int[][] pHeight = {
        {2},
        {4, 1},
        {3, 2, 3, 2},
        {3, 2, 3, 2},
        {3, 2, 3, 2},
        {2, 3},
        {2, 3}
    };
    private static int[][][] pBottom = {
        {{0, 0}},
        {{0}, {0, 0, 0, 0}},
        {{0, 0}, {0, 1, 1}, {2, 0}, {0, 0, 0}},
        {{0, 0}, {0, 0, 0}, {0, 2}, {1, 1, 0}},
        {{0, 1}, {1, 0, 1}, {1, 0}, {0, 0, 0}},
        {{0, 0, 1}, {1, 0}},
        {{1, 0, 0}, {0, 1}}
    };
    private static int[][][] pTop = {
        {{2, 2}},
        {{4}, {1, 1, 1, 1}},
        {{3, 1}, {2, 2, 2}, {3, 3}, {1, 1, 2}},
        {{1, 3}, {2, 1, 1}, {3, 3}, {2, 2, 2}},
        {{3, 2}, {2, 2, 2}, {2, 3}, {1, 2, 1}},
        {{1, 2, 2}, {3, 2}},
        {{2, 2, 1}, {2, 3}}
    };
}
