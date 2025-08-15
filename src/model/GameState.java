package model;

import java.awt.Color;
import java.util.Random;
import java.util.function.Supplier;

/**
 *  Core model for the Tetris game.
 */
public class GameState {
    public static final int ROWS = 20;
    public static final int COLUMNS = 10;

    public static final int DROP_MS_START = 550;
    public static final int DROP_MS_FAST = 50;

    public static final int WIN_LINES = 40;

    private final int[][] board = new int[ROWS][COLUMNS];
    private final Color[] palette = new Color[7];

    private final Random rng = new Random();
    private Piece cur;
    private Piece next;

    private boolean paused = false;
    private boolean gameOver = false;
    private boolean win = false;

    private int score = 0;
    private int linesCleared = 0;
    private int level = 1;
    private int dropDelay = DROP_MS_START;

    private static final Supplier<BaseTetromino>[] FACTORY = new Supplier[]{
            IShape::new, JShape::new, LShape::new, OShape::new, SShape::new, TShape::new, ZShape::new
    };

    /**
     * Initialize board, palette, and start a new game.
     */
    public GameState() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLUMNS; c++)
                board[r][c] = -1;

        palette[0] = new Color(0, 240, 240);
        palette[1] = new Color(0, 0, 255);
        palette[2] = new Color(255, 165, 0);
        palette[3] = new Color(255, 255, 0);
        palette[4] = new Color(0, 255, 0);
        palette[5] = new Color(160, 0, 240);
        palette[6] = new Color(255, 0, 0);

        reset();
    }

    /**
     * Reset game state to starting values.
     */
    public void reset() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLUMNS; c++)
                board[r][c] = -1;
        score = 0;
        linesCleared = 0;
        level = 1;
        dropDelay = DROP_MS_START;
        paused = false;
        gameOver = false;
        win = false;
        cur = randomPiece();
        next = randomPiece();
        if (collides(cur, cur.row, cur.col, cur.rot)) {
            gameOver = true;
        }
    }

    /**
     * This adds 1 gravity tick
     * @return
     */
    public boolean stepGravity() {
        if (paused || gameOver)
            return false;
        return tryMove(cur.row + 1, cur.col, cur.rot, true);
    }

    /**
     * Instandly drops the peice
     */
    public void hardDrop() {
        if (paused || gameOver) return;
        while (!collides(cur, cur.row + 1, cur.col, cur.rot)) cur.row++;
        lockPiece();
    }

    /**
     * Handles the moment, left, right soft drop
     */
        public void moveLeft() {
        if (!paused && !gameOver)
            tryMove(cur.row, cur.col - 1, cur.rot, false);
    }
        public void moveRight() {
        if (!paused && !gameOver)
            tryMove(cur.row, cur.col + 1, cur.rot, false);
    }
        public void softDropOnce() {
        if (!paused && !gameOver)
            tryMove(cur.row + 1, cur.col, cur.rot, true);
    }

    /**
     * Rotate piece clockwise
     */
    public void rotateCW() {
        if (paused || gameOver) return;
        int nr = (cur.rot + 1) % 4;
        if (!collides(cur, cur.row, cur.col, nr))
            cur.rot = nr;
        else if (!collides(cur, cur.row, cur.col - 1, nr)){
            cur.col--; cur.rot = nr;
        }
        else if (!collides(cur, cur.row, cur.col + 1, nr)){
            cur.col++; cur.rot = nr;
        }
    }

    /**
     * toggle pause
     */
    public void togglePause() {
        if (!gameOver)
            paused = !paused;
    }

    /**
     * Getters
     * @return
     */
    public int[][] getBoard() {
        return board;
    }

    public Color[] getPalette() {
        return palette;
    }

    public Piece getNext() {
        return next;
    }

    public Piece getCurrent() {
        return cur;
    }

    public boolean isPaused() {
        return paused;
    }
    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWin() {
        return win;
    }

    public int getScore() {
        return score;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public int getLevel() {
        return level;
    }
    public int getDropDelay() {
        return dropDelay;
    }

    /**
     * This picks a new random tetromino
     * @return
     */
    private Piece randomPiece() {
        BaseTetromino t = FACTORY[rng.nextInt(FACTORY.length)].get();
        int rot = 0;
        int spawnCol = (COLUMNS - t.getRotations()[0][0].length) / 2;
        return new Piece(t, rot, 0, spawnCol);
    }

    /**
     * Check if a piece at a given row,col,rot collides with board boundaries or filled cell
     * @param p
     * @param newRow
     * @param newCol
     * @param newRot
     * @return
     */
    private boolean collides(Piece p, int newRow, int newCol, int newRot) {
        int[][] s = p.type.getRotations()[newRot];
        int h = s.length, w = s[0].length;
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                if (s[r][c] == 0) continue;
                int br = newRow + r, bc = newCol + c;
                if (bc < 0 || bc >= COLUMNS || br >= ROWS) return true;
                if (br < 0) continue;
                if (board[br][bc] != -1) return true;
            }
        }
        return false;
    }

    /**
     * Attempt to move current piece, If free, updates row/col/rot, If blocked and falling, locks the piece.
     * @param nr
     * @param nc
     * @param rot
     * @param lockIfBlocked
     * @return
     */
    private boolean tryMove(int nr, int nc, int rot, boolean lockIfBlocked) {
        if (!collides(cur, nr, nc, rot)) {
            cur.row = nr;
            cur.col = nc;
            cur.rot = rot;
            return  true;
        }     else if (lockIfBlocked && nr == cur.row + 1 && nc == cur.col) {
            lockPiece();
        }
        return false;
    }

    /**
     *  Lock current piece onto the board, clear lines, update score, then spawn next piece
     */
    private void lockPiece() {
        int idx = cur.type.getIndex();
        int[][] s = cur.shape();
        for (int r = 0; r < s.length; r++) {
            for (int c = 0; c < s[0].length; c++) {
                if (s[r][c] == 0) continue;
                int br = cur.row + r, bc = cur.col + c;
                if (br >= 0 && br < ROWS && bc >= 0 && bc < COLUMNS) {
                    board[br][bc] = idx;
                } else {
                    gameOver = true; // locked out of bounds
                }
            }
        }
        int cleared = clearLines();
        updateScore(cleared);
        if (!gameOver) spawnNext();
    }

    /**
     * Replace current piece with next and gives a new preview.
     */
    private void spawnNext() {
        cur = next;
        next = randomPiece();
        if (collides(cur, cur.row, cur.col,cur.rot))
            gameOver = true;
    }

    /**
     * Scan board for full lines, clear them, shift above rows down, and update win condition
     * @return
     */
    private int clearLines() {
        int cleared = 0;
        for (int r = ROWS -1 ; r >= 0; r--) {
            boolean full = true;
            for (int c = 0; c < COLUMNS; c++)
                if (board[r][c] == -1) {
                    full = false;
                }
            if (full) {
                cleared++;
                for (int rr = r; rr > 0; rr--) System.arraycopy(board[rr - 1], 0, board[rr], 0, COLUMNS);
                for (int c = 0; c < COLUMNS; c++) board[0][c] = -1;
                r++;
            }
        }
        linesCleared += cleared;
        if (linesCleared >= WIN_LINES) { win = true; gameOver = true; }
        return cleared;
    }

    /**
     * Add points for cleared lines, adjust level and drop speed
     * @param cleared
     */
    private void updateScore(int cleared) {
        int add = switch (cleared) {
            case 1 -> 100;
            case 2 -> 300;
            case 3 -> 500;
            case 4 -> 800;
            default -> 0;
        };
        score += add;
        int newLevel = 1 + linesCleared / 10;
        if (newLevel != level) {
            level = newLevel;
            dropDelay = Math.max(90,DROP_MS_START - (level - 1) * 50);
        }
    }
}
